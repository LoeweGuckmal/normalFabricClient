package ch.loewe.normal_use_client.fabricclient.cape;

import ch.loewe.normal_use_client.fabricclient.client.FabricClientClient;
import ch.loewe.normal_use_client.fabricclient.modmenu.Config;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.apache.commons.io.FileUtils;

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

            try {
                logger.info("Getting profile for {}", playerHandler.getPlayerUUID());
                String var10002 = playerHandler.getPlayerUUID().toString();
                URL url = new URL("https://minecraftcapes.net/profile/" + var10002.replace("-", ""));
                HttpURLConnection httpurlconnection = (HttpURLConnection)url.openConnection(MinecraftClient.getInstance().getNetworkProxy());
                httpurlconnection.setDoInput(true);
                httpurlconnection.setDoOutput(false);
                httpurlconnection.connect();
                if (httpurlconnection.getResponseCode() / 100 == 2) {
                    Reader reader = new InputStreamReader(httpurlconnection.getInputStream(), StandardCharsets.UTF_8);
                    readProfile(playerHandler, reader);
                    reader.close();
                } else {
                    loadOfflineProfile(playerHandler);
                    logger.warn("minecraftcapes.net returned a {}", httpurlconnection.getResponseCode());
                }
            } catch (IOException var4) {
                loadOfflineProfile(playerHandler);
                logger.warn("No connection to minecraftcapes.net detected");
            }

        });
        playerDownload.setDaemon(true);
        playerDownload.start();
    }

    private static void readProfile(PlayerHandler playerHandler, Reader reader) {
        DownloadManager.ProfileResult profileResult = (DownloadManager.ProfileResult)(new Gson()).fromJson(reader, DownloadManager.ProfileResult.class);
        //playerHandler.setHasCapeGlint(profileResult.capeGlint);
        playerHandler.setHasCapeGlint(Config.getHasCapeGlint());
        playerHandler.setUpsideDown(profileResult.upsideDown);
        if (profileResult.textures.get("cape") != null) {
            playerHandler.applyCape((String)profileResult.textures.get("cape"));
        }

        if (profileResult.textures.get("ears") != null) {
            playerHandler.applyEars((String)profileResult.textures.get("ears"));
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
        File profileDirectory = new File(MinecraftClient.getInstance().runDirectory + "/config/minecraftcapes" + "/profile");
        File profileFile = new File(new File(profileDirectory, fileName.length() > 2 ? fileName.substring(0, 2) : "xx"), fileName);
        if (profileFile.exists()) {
            try {
                Reader reader = new FileReader(profileFile);
                readProfile(playerHandler, reader);
                reader.close();
            } catch (Exception var5) {
                logger.error("Cache corrupt for {}", fileName);
                profileFile.delete();
            }
        }

    }

    private static class ProfileResult {
        private boolean capeGlint = false;
        private boolean upsideDown = false;
        private Map<String, String> textures = null;

        private ProfileResult() {
        }
    }
}

