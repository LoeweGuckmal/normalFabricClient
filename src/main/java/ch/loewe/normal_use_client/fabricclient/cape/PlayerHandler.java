package ch.loewe.normal_use_client.fabricclient.cape;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

import static ch.loewe.normal_use_client.fabricclient.cape.DownloadManager.isAnimated;

public class PlayerHandler {
    private static final HashMap<UUID, PlayerHandler> instances = new HashMap<>();
    private boolean hasStaticCape = false;
    private boolean hasEars = false;
    private boolean hasAnimatedCape = false;
    private Boolean showCape = true;
    private Boolean forceShowElytra = false;
    private Boolean forceHideElytra = false;
    private Boolean hasCapeGlint = false;
    private boolean upsideDown = false;
    private Boolean hasInfo = false;
    private UUID playerUUID;
    private Int2ObjectMap<NativeImage> animatedCape;
    private long lastFrameTime = 0L;
    private int lastFrame = 0;
    private final int capeInterval = 100;

    public PlayerHandler(PlayerEntity player) {
        this.playerUUID = player.getUuid();
        instances.put(this.playerUUID, this);
    }

    public static PlayerHandler getFromPlayer(PlayerEntity player) {
        PlayerHandler playerHandler = instances.get(player.getUuid());
        return playerHandler == null ? new PlayerHandler(player) : playerHandler;
    }

    private NativeImage readTexture(String textureBase64) {
        try {
            byte[] imgBytes = Base64.getDecoder().decode(textureBase64);
            ByteArrayInputStream bias = new ByteArrayInputStream(imgBytes);
            return NativeImage.read(bias);
        } catch (IOException ignored) {
            return null;
        }
    }

    public void applyCape(String cape) {
        NativeImage capeImage = this.readTexture(cape);
        if (capeImage != null) {
            if (isAnimated(capeImage)) {
                Int2ObjectMap<NativeImage> animatedCapeFrames = new Int2ObjectOpenHashMap<>();
                int framesAmount = capeImage.getHeight() / (capeImage.getWidth() / 2);
                int imageWidth = capeImage.getWidth();
                int imageHeigt = (capeImage.getWidth() / 2);

                for (int currentFrame = 0; currentFrame < framesAmount; ++currentFrame) {
                    NativeImage frame = new NativeImage(imageWidth, imageHeigt, true);

                    for (int x = 0; x < frame.getWidth(); ++x) {
                        for (int y = 0; y < frame.getHeight(); ++y) {
                            frame.setColor(x, y, capeImage.getColor(x, y + currentFrame * imageHeigt));
                        }
                    }

                    animatedCapeFrames.put(currentFrame, frame);
                }

                this.setHasInfo(true);
                this.setAnimatedCape(animatedCapeFrames);
            } else {
                int width = 64;
                int height = 32;
                int imageWidth = capeImage.getWidth();
                int imageHeight = capeImage.getHeight();

                while (width < imageWidth || height < imageHeight) {
                    height *= 2;
                    width *= 2;
                }

                NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);

                for (int x = 0; x < capeImage.getWidth(); ++x) {
                    for (int y = 0; y < capeImage.getHeight(); ++y) {
                        imgNew.setColor(x, y, capeImage.getColor(x, y));
                    }
                }

                capeImage.close();
                this.setHasInfo(true);
                this.applyTexture(new Identifier("loewe", "capes/" + this.playerUUID), imgNew);
                this.setHasStaticCape(true);
                this.setHasAnimatedCape(false);
            }
        }

    }

    public void applyEars(String ears) {
        NativeImage earImage = this.readTexture(ears);
        this.applyTexture(new Identifier("loewe", "ears/" + this.playerUUID), earImage);
        this.setHasEars(true);
    }

    public void setAnimatedCape(Int2ObjectMap<NativeImage> animatedCape) {
        this.animatedCape = animatedCape;
        this.setHasStaticCape(false);
        this.setHasAnimatedCape(true);
        this.loadFramesToResource();
    }
    private void loadFramesToResource() {
        this.getAnimatedCape().forEach((integer, nativeImage) -> {
            Identifier currentResource = new Identifier("loewe", String.format("capes/%s/%d", this.playerUUID, integer));
            this.applyTexture(currentResource, nativeImage);
        });
    }
    private Identifier getFrame() {
        long time = System.currentTimeMillis();
        if (time > this.lastFrameTime + (long)this.capeInterval) {
            int currentFrameNo = this.lastFrame + 1 > this.getAnimatedCape().size() - 1 ? 0 : this.lastFrame + 1;
            this.lastFrame = currentFrameNo;
            this.lastFrameTime = time;
            return new Identifier("loewe", String.format("capes/%s/%d", this.playerUUID, currentFrameNo));
        } else {
            return new Identifier("loewe", String.format("capes/%s/%d", this.playerUUID, this.lastFrame));
        }
    }

    public Identifier getCapeLocation() {
        return this.hasStaticCape ? new Identifier("loewe", "capes/" + this.playerUUID) : (this.hasAnimatedCape ? this.getFrame() : null);
    }
    public Identifier getEarLocation() {
        return this.hasEars ? new Identifier("loewe", "ears/" + this.playerUUID) : null;
    }

    private void applyTexture(Identifier resourceLocation, NativeImage nativeImage) {
        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().getTextureManager().registerTexture(resourceLocation, new NativeImageBackedTexture(nativeImage)));
    }
    public void applyTextureP(Identifier resourceLocation, NativeImage nativeImage){
        applyTexture(resourceLocation, nativeImage);
    }

    //get/set
    public String toString() {
        return "PlayerHandler{hasStaticCape=" + this.hasStaticCape + ", hasEars=" + this.hasEars + ", hasAnimatedCape=" + this.hasAnimatedCape + ", showCape=" + this.showCape + ", hasCapeGlint=" + this.hasCapeGlint + ", upsideDown=" + this.upsideDown + ", hasInfo=" + this.hasInfo + ", playerUUID=" + this.playerUUID + ", animatedCape=" + this.animatedCape + ", lastFrameTime=" + this.lastFrameTime + ", lastFrame=" + this.lastFrame + ", capeInterval=" + this.capeInterval + "}";
    }

    public void setHasStaticCape(boolean hasStaticCape) {
        this.hasStaticCape = hasStaticCape;
    }
    public void setHasEars(boolean hasEars) {
        this.hasEars = hasEars;
    }
    public void setHasAnimatedCape(boolean hasAnimatedCape) {
        this.hasAnimatedCape = hasAnimatedCape;
    }

    public Boolean getShowCape() {
        return this.showCape;
    }
    public void setShowCape(Boolean showCape) {
        this.showCape = showCape;
    }

    public Boolean getForceShowElytra() {
        return this.forceShowElytra;
    }
    public void setForceShowElytra(Boolean forceShowElytra) {
        this.forceShowElytra = forceShowElytra;
    }

    public Boolean getForceHideElytra() {
        return this.forceHideElytra;
    }
    public void setForceHideElytra(Boolean forceHideElytra) {
        this.forceHideElytra = forceHideElytra;
    }

    public Boolean getHasCapeGlint() {
        return this.hasCapeGlint;
    }
    public void setHasCapeGlint(Boolean hasCapeGlint) {
        this.hasCapeGlint = hasCapeGlint;
    }

    public boolean isUpsideDown() {
        return this.upsideDown;
    }
    public void setUpsideDown(boolean upsideDown) {
        this.upsideDown = upsideDown;
    }

    public Boolean getHasInfo() {
        return this.hasInfo;
    }
    public void setHasInfo(Boolean hasInfo) {
        this.hasInfo = hasInfo;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }
    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public Int2ObjectMap<NativeImage> getAnimatedCape() {
        return this.animatedCape;
    }
}

