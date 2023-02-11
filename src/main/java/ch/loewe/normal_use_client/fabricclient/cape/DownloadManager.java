package ch.loewe.normal_use_client.fabricclient.cape;

import ch.loewe.normal_use_client.fabricclient.modmenu.Config;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import static ch.loewe.normal_use_client.fabricclient.client.FabricClientClient.logger;

public class DownloadManager {
    public DownloadManager() {
    }

    public static void prepareDownload(PlayerEntity player, boolean doRefresh) {
        ClientPlayerEntity localPlayer = MinecraftClient.getInstance().player;
        if (player.getUuid().version() == 4 || localPlayer == null || localPlayer.getUuid().equals(player.getUuid())) {
            PlayerHandler playerHandler = PlayerHandler.getFromPlayer(player);
            if (player.getUuid().version() != 4 && !playerHandler.getHasInfo() && !doRefresh) {
                playerHandler.setHasInfo(true);
                Thread playerDownload = new Thread(() -> {
                    UUID uuid = MinecraftApi.getUUID(player.getEntityName());
                    if (uuid != null) {
                        playerHandler.setPlayerUUID(uuid);
                        downloadProfile(playerHandler);
                    } else if (Config.getCapeFromFile()) {
                        playerHandler.setPlayerUUID(player.getUuid());
                        downloadProfile(playerHandler);
                    }
                });
                playerDownload.setDaemon(true);
                playerDownload.start();
            } else {
                if (playerHandler.getHasInfo() && !doRefresh) {
                    return;
                }

                downloadProfile(playerHandler);
            }

        }
    }

    private static void downloadProfile(PlayerHandler playerHandler) {
        Thread playerDownload = new Thread(() -> {
            playerHandler.setHasInfo(true);
            if (Config.getCapeFromFile())
                loadOfflineProfilePng(playerHandler);

            try {
                //logger.info("Getting profile for {}", playerHandler.getPlayerUUID());
                String var10002 = playerHandler.getPlayerUUID().toString();
                URL url = new URL("https://minecraftcapes.net/profile/" + var10002.replace("-", ""));
                HttpURLConnection httpurlconnection = (HttpURLConnection)url.openConnection(MinecraftClient.getInstance().getNetworkProxy());
                httpurlconnection.setDoInput(true);
                httpurlconnection.setDoOutput(false);
                httpurlconnection.connect();
                if (httpurlconnection.getResponseCode() / 100 == 2) {
                    Reader reader = new InputStreamReader(httpurlconnection.getInputStream(), StandardCharsets.UTF_8);
                    if (Config.getCapeFromFile())
                        loadOfflineProfilePng(playerHandler);
                    else
                        readProfile(playerHandler, reader);
                    reader.close();
                } else {
                    if (Config.getCapeFromFile())
                        loadOfflineProfilePng(playerHandler);
                    else
                        loadOfflineProfile(playerHandler);
                    //logger.warn("minecraftcapes.net returned a {}", httpurlconnection.getResponseCode());
                }
            } catch (IOException var4) {
                if (Config.getCapeFromFile())
                    loadOfflineProfilePng(playerHandler);
                else
                    loadOfflineProfile(playerHandler);
                //logger.warn("No connection to minecraftcapes.net detected");
            }

        });
        playerDownload.setDaemon(true);
        playerDownload.start();
    }

    private static void readProfile(PlayerHandler playerHandler, Reader reader) {
        DownloadManager.ProfileResult profileResult = (new Gson()).fromJson(reader, ProfileResult.class);
        playerHandler.setHasCapeGlint(Config.getHasCapeGlint());
        playerHandler.setUpsideDown(profileResult.upsideDown);
        if (profileResult.textures.get("cape") != null) {
            playerHandler.applyCape(profileResult.textures.get("cape"));
        }

        if (profileResult.textures.get("ears") != null) {
            playerHandler.applyEars(profileResult.textures.get("ears"));
        }

        cacheProfile(playerHandler, profileResult);
    }

    private static void cacheProfile(PlayerHandler playerHandler, DownloadManager.ProfileResult profileResult) {
        String fileName = playerHandler.getPlayerUUID().toString();
        String profileJson = (new Gson()).toJson(profileResult);
        File profileDirectory = new File(MinecraftClient.getInstance().runDirectory + "/config/minecraftcapes" + "/profile");
        File profileFile = new File(new File(profileDirectory, fileName.length() > 2 ? fileName.substring(0, 2) : "xx"), fileName);

        try {
            FileUtils.writeStringToFile(profileFile, profileJson, StandardCharsets.UTF_8);
        } catch (Exception var7) {
            logger.error("Error writing cache file");
            var7.printStackTrace();
        }

    }

    private static void loadOfflineProfile(PlayerHandler playerHandler) {
        String fileName = playerHandler.getPlayerUUID().toString();
        File profileDirectory = new File(MinecraftClient.getInstance().runDirectory + "/config/loewe" + "/cape");
        File profileFile = new File(new File(profileDirectory, fileName.length() > 2 ? fileName.substring(0, 2) : "xx"), fileName);
        if (profileFile.exists()) {
            try {
                Reader reader = new FileReader(profileFile);
                readProfile(playerHandler, reader);
                reader.close();
            } catch (Exception var5) {
                //logger.error("Cache corrupt for {}", fileName);
                profileFile.delete();
            }
        }

    }
    private static void loadOfflineProfilePng(PlayerHandler playerHandler) {
        String fileName = "cape.png";
        File profileDirectory = new File(MinecraftClient.getInstance().runDirectory + "/config/loewe" + "/cape");
        profileDirectory.mkdirs();
        File profileFile = new File(profileDirectory, fileName);

        playerHandler.setHasCapeGlint(Config.getHasCapeGlint());
        if (profileFile.exists()) {
            try {
                BufferedImage capeImage = ImageIO.read(profileFile);
                int imageWidth = 64;
                int imageHeight = 32;
                int currentFrame = capeImage.getWidth();
                if (capeImage.getHeight() != capeImage.getWidth() / 2) {
                    Int2ObjectMap<NativeImage> animatedCapeFrames = new Int2ObjectOpenHashMap();
                    imageHeight = capeImage.getHeight() / (capeImage.getWidth() / 2);

                    for(currentFrame = 0; currentFrame < imageHeight; ++currentFrame) {
                        NativeImage frame = new NativeImage(capeImage.getWidth(), capeImage.getWidth() / 2, true);

                        for(int x = 0; x < frame.getWidth(); ++x) {
                            for(int y = 0; y < frame.getHeight(); ++y) {
                                frame.setColor(x, y, ABGRfromARGB(capeImage.getRGB(x, y + currentFrame * (capeImage.getWidth() / 2))));
                            }
                        }

                        animatedCapeFrames.put(currentFrame, frame);
                    }

                    playerHandler.setAnimatedCape(animatedCapeFrames);
                    //logger.info("Animated cape loaded for {}", playerHandler.getPlayerUUID());
                } else {

                    for (int x = capeImage.getHeight(); imageWidth < currentFrame || imageHeight < x; imageHeight *= 2) {
                        imageWidth *= 2;
                    }

                    NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);

                    for (int x = 0; x < capeImage.getWidth(); ++x) {
                        for (int y = 0; y < capeImage.getHeight(); ++y) {
                            imgNew.setColor(x, y, ABGRfromARGB(capeImage.getRGB(x, y)));
                        }
                    }

                    playerHandler.applyTextureP(new Identifier("loewe", "capes/" + playerHandler.getPlayerUUID()), imgNew);
                    playerHandler.setHasStaticCape(true);
                    playerHandler.setHasAnimatedCape(false);
                    //logger.info("Static cape loaded for {}", playerHandler.getPlayerUUID());
                }
            } catch (Exception var5) {
                //logger.error("Cache corrupt for {}", fileName);
                profileFile.delete();
            }
        }
    }

    public static class ProfileResult {
        private boolean capeGlint = false;
        private boolean upsideDown = false;
        public Map<String, String> textures = null;

        private ProfileResult() {
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

