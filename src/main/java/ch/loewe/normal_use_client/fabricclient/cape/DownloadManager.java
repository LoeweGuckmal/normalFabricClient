package ch.loewe.normal_use_client.fabricclient.cape;

import ch.loewe.normal_use_client.fabricclient.modmenu.Config;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import static ch.loewe.normal_use_client.fabricclient.client.FabricClientClient.logger;
import static ch.loewe.normal_use_client.fabricclient.client.FabricClientClient.mc;

public class DownloadManager {
    public static boolean wait = false;
    public static boolean clickedDuringWait = false;
    public static boolean firstClick = false;
    public DownloadManager() {
    }

    public static void prepareDownload(PlayerEntity player, boolean doRefresh, boolean doHardRefresh) {
        if (player.getUuid().version() == 4 || isLocalPlayer(player)) { //only real people
            PlayerHandler playerHandler = PlayerHandler.getFromPlayer(player);
            if (doHardRefresh || !playerHandler.getHasInfo()) { //set UUID
                Thread playerDownload = new Thread(() -> {
                    UUID uuid = MinecraftApi.getUUID(String.valueOf(player.getName()));
                    if (uuid != null) { //is known and online
                        playerHandler.setPlayerUUID(uuid);
                        downloadProfile(playerHandler);
                    }
                    else if (isLocalPlayer(player)) {
                        /*is local player
                        wait = true;
                        firstClick = false;
                        clickedDuringWait = mc.options.getPerspective().equals(Perspective.THIRD_PERSON_BACK);
                        mc.options.setPerspective(Perspective.FIRST_PERSON);*/
                        playerHandler.setPlayerUUID(player.getUuid());
                        loadOfflineProfilePng(playerHandler);
                    }
                });
                playerDownload.setDaemon(true);
                playerDownload.start();
            } else {
                if (playerHandler.getHasInfo() && doRefresh) { //doRefresh
                    downloadProfile(playerHandler);
                }
            }
        }
    }

    public static boolean isLocalPlayer(PlayerEntity player){
        /*if (mc.player != null && !mc.player.getUuid().equals(player.getUuid()) && mc.player.getEntityName().equals(player.getEntityName())) {
            new AccountListScreen(mc.currentScreen).loginOffline(new OfflineAccount(player.getEntityName(), Auth.resolveUUID(player.getEntityName())));
        }*/
        return mc.player == null || mc.player.getUuid().equals(player.getUuid()) || mc.player.getName().equals(player.getName());
    }

    private static void downloadProfile(PlayerHandler playerHandler) {
        Thread playerDownload = new Thread(() -> {
            if (Config.getCapeFromFile() && isLocalPlayer(playerHandler.getPlayer())) {
                loadOfflineProfilePng(playerHandler);
            }
            else try { //////////////////////////////////////////////////////////
                String uuidString = playerHandler.getPlayerUUID().toString();
                URL url = new URL("https://api.minecraftcapes.net/profile/" + uuidString.replace("-", ""));
                HttpURLConnection conn = (HttpURLConnection)url.openConnection(mc.getNetworkProxy());
                conn.setDoInput(true);
                conn.setDoOutput(false);
                conn.connect();
                if (conn.getResponseCode() / 100 == 2) {
                    Reader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
                    ProfileResult profileResult = (new Gson()).fromJson(reader, ProfileResult.class);
                    //logger.info(profileResult.toString());
                    if (!profileResult.textures.isEmpty()) {
                        readProfile(playerHandler, profileResult, true);
                        reader.close();
                        return;
                    }
                    reader.close();
                }
                loadOfflineProfile(playerHandler);
            } catch (IOException ignored) {
                loadOfflineProfile(playerHandler);
            }

        });
        playerDownload.setDaemon(true);
        playerDownload.start();
    }

    private static void readProfile(PlayerHandler playerHandler, ProfileResult profileResult, boolean cache) {
        playerHandler.setHasInfo(true);
        //logger.info(profileResult.toString() + isLocalPlayer(playerHandler.getPlayer()));
        playerHandler.setHasCapeGlint(profileResult.capeGlint);
        playerHandler.setUpsideDown(profileResult.upsideDown);
        boolean cape = profileResult.textures.get("cape") != null;
        boolean ears = profileResult.textures.get("ears") != null;
        if (isLocalPlayer(playerHandler.getPlayer())) {
            playerHandler.setHasCapeGlint(Config.getHasCapeGlint());
        }
        if (!cape && !ears) {
            if (isLocalPlayer(playerHandler.getPlayer()))
                loadOfflineProfilePng(playerHandler);
        } else {
            playerHandler.setDownloadedPngLast(false);
            if (cape) {
                playerHandler.applyCape(profileResult.textures.get("cape"));
            }
            if (ears) {
                playerHandler.applyEars(profileResult.textures.get("ears"));
            }
            if (cache && cape) {
                cacheProfile(playerHandler, profileResult);
            }
        }
    }

    private static void cacheProfile(PlayerHandler playerHandler, ProfileResult profileResult) {
        String fileName = playerHandler.getPlayerUUID().toString();
        String profileJson = (new Gson()).toJson(profileResult);
        File profileDirectory = new File(mc.runDirectory + "/config/loewe/cape/cache");
        if(profileDirectory.mkdirs())
            logger.warn("created directory \"/config/loewe/cape/cache\"");
        File profileFile = new File(new File(profileDirectory, shortUuid(fileName)), fileName);
        try {
            FileUtils.writeStringToFile(profileFile, profileJson, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
        }
    }

    private static void loadOfflineProfile(PlayerHandler playerHandler) {
        try {
            String fileName = playerHandler.getPlayerUUID().toString();
            File profileDirectory = new File(mc.runDirectory + "/config/loewe/cape/cache");
            if(profileDirectory.mkdirs())
                logger.warn("created directory \"/config/loewe/cape/cache\"");
            File profileFile = new File(new File(profileDirectory, shortUuid(fileName)), fileName);
            if (profileFile.exists()) {
                try {
                    Reader reader = new FileReader(profileFile);
                    ProfileResult profileResult = (new Gson()).fromJson(reader, ProfileResult.class);
                    if (!profileResult.textures.isEmpty()) {
                        readProfile(playerHandler, profileResult, false);
                        reader.close();
                        return;
                    }
                    reader.close();
                } catch (Exception ignored) {
                    profileFile.delete();
                }
            }
            if (isLocalPlayer(playerHandler.getPlayer())) {
                loadOfflineProfilePng(playerHandler);
            }
        } catch (Exception ignored){}
    }
    private static void loadOfflineProfilePng(PlayerHandler playerHandler) {
        playerHandler.setDownloadedPngLast(true);
        if (isLocalPlayer(playerHandler.getPlayer())) {
            /*if (!wait) {
                wait = true;
                firstClick = false;
                clickedDuringWait = mc.options.getPerspective().equals(Perspective.THIRD_PERSON_BACK);
                mc.options.setPerspective(Perspective.FIRST_PERSON);
            }*/
            String fileName = "cape.png";
            File profileDirectory = new File(mc.runDirectory + "/config/loewe/cape");
            if (profileDirectory.mkdirs())
                logger.warn("created directory \"/config/loewe/cape\"");
            File profileFile = new File(profileDirectory, fileName);

            playerHandler.setHasCapeGlint(Config.getHasCapeGlint());
            if (profileFile.exists()) {
                try {
                    BufferedImage capeImage = ImageIO.read(profileFile);
                    if (isAnimated(capeImage)) { //animated
                        Int2ObjectMap<NativeImage> animatedCapeFrames = new Int2ObjectOpenHashMap<>();
                        int framesAmount = capeImage.getHeight() / (capeImage.getWidth() / 2);
                        int imageWidth = capeImage.getWidth();
                        int imageHeigt = (capeImage.getWidth() / 2);

                        for (int currentFrame = 0; currentFrame < framesAmount; ++currentFrame) {
                            NativeImage frame = new NativeImage(imageWidth, imageHeigt, true);

                            for (int x = 0; x < frame.getWidth(); ++x) {
                                for (int y = 0; y < frame.getHeight(); ++y) {
                                    frame.setColorArgb(x, y, capeImage.getRGB(x, y + currentFrame * imageHeigt));
                                }
                            }

                            animatedCapeFrames.put(currentFrame, frame);
                        }

                        playerHandler.setHasInfo(true);
                        playerHandler.setAnimatedCape(animatedCapeFrames);
                    } else { //static
                        int width = 64;
                        int height = 32;
                        int imageWidth = capeImage.getWidth();
                        int imageHeight = capeImage.getHeight();
                        while (width < imageWidth || height < imageHeight) {
                            height *= 2;
                            width *= 2;
                        }

                        NativeImage imgNew = new NativeImage(width, height, true);

                        for (int x = 0; x < imageWidth; ++x) {
                            for (int y = 0; y < imageHeight; ++y) {
                                imgNew.setColorArgb(x, y, capeImage.getRGB(x, y));
                            }
                        }

                        playerHandler.setHasInfo(true);
                        playerHandler.applyTextureP(Identifier.of("loewe", "capes/" + playerHandler.getPlayerUUID()), imgNew);
                        playerHandler.setHasStaticCape(true);
                        playerHandler.setHasAnimatedCape(false);
                    }
                } catch (Exception ignored) {
                    profileFile.delete();
                }
            }
        }
        /*wait = false;
        if (clickedDuringWait) {
            mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            clickedDuringWait = false;
        }*/
    }

    public static boolean isAnimated(BufferedImage image) {
        return image.getHeight() != (image.getWidth() / 2);
    }
    public static boolean isAnimated(NativeImage image) {
        return image.getHeight() != (image.getWidth() / 2);
    }
    public static String shortUuid(String uuid){
        return uuid.length() > 2 ? uuid.substring(0, 2) : "xx";
    }

    public static class ProfileResult {
        private boolean capeGlint = false;
        private boolean upsideDown = false;
        public Map<String, String> textures = null;

        private ProfileResult() {
        }

        @Override
        public String toString(){
            return "capeGlint: " + capeGlint + ", upsideDown: " + upsideDown + ", textures-size: " + textures.size();
        }
    }
    public static int ABGR(int r, int g, int b, int a) {
        return ((a & 255) << 24) | ((b & 255) << 16) | ((g & 255) << 8) | (r & 255);
    }
    public static int ABGRfromARGB(int px) {
        int a = (px >> 24)& 0xff;
        int r = (px >> 16)& 0xff;
        int g = (px >> 8)& 0xff;
        int b = (px)& 0xff;
        return ABGR(r, g, b, a);
    }
}

