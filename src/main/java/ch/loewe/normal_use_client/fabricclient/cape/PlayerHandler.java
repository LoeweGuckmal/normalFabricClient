package ch.loewe.normal_use_client.fabricclient.cape;

import ch.loewe.normal_use_client.fabricclient.client.FabricClientClient;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import static ch.loewe.normal_use_client.fabricclient.client.FabricClientClient.logger;

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
            //logger.info(String.valueOf(Arrays.equals(Base64.getEncoder().encode(NativeImage.read(bias).getBytes()), imgBytes)));
            return NativeImage.read(bias);
        } catch (IOException var4) {
            logger.error(var4.getMessage());
            var4.printStackTrace();
            return null;
        }
    }

    public void applyCape(String cape) {
        NativeImage capeImage = this.readTexture(cape);
        int imageHeight;
        int currentFrame;
        int x;
        if (capeImage.getHeight() != capeImage.getWidth() / 2) {
            Int2ObjectMap<NativeImage> animatedCapeFrames = new Int2ObjectOpenHashMap();
            imageHeight = capeImage.getHeight() / (capeImage.getWidth() / 2);

            for(currentFrame = 0; currentFrame < imageHeight; ++currentFrame) {
                NativeImage frame = new NativeImage(capeImage.getWidth(), capeImage.getWidth() / 2, true);

                for(x = 0; x < frame.getWidth(); ++x) {
                    for(int y = 0; y < frame.getHeight(); ++y) {
                        frame.setColor(x, y, capeImage.getColor(x, y + currentFrame * (capeImage.getWidth() / 2)));
                    }
                }

                animatedCapeFrames.put(currentFrame, frame);
            }

            this.setAnimatedCape(animatedCapeFrames);
            //logger.info("Animated cape loaded for {}", this.playerUUID);
        } else {
            int imageWidth = 64;
            imageHeight = 32;
            currentFrame = capeImage.getWidth();

            for(x = capeImage.getHeight(); imageWidth < currentFrame || imageHeight < x; imageHeight *= 2) {
                imageWidth *= 2;
            }

            NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);

            for(x = 0; x < capeImage.getWidth(); ++x) {
                for(int y = 0; y < capeImage.getHeight(); ++y) {
                    imgNew.setColor(x, y, capeImage.getColor(x, y));
                }
            }

            capeImage.close();
            this.applyTexture(new Identifier("loewe", "capes/" + this.playerUUID), imgNew);
            this.setHasStaticCape(true);
            this.setHasAnimatedCape(false);
            //logger.info("Static cape loaded for {}", this.playerUUID);
        }

    }

    public void applyEars(String ears) {
        NativeImage earImage = this.readTexture(ears);
        this.applyTexture(new Identifier("loewe", "ears/" + this.playerUUID), earImage);
        this.setHasEars(true);
    }

    public void setAnimatedCape(Int2ObjectMap<NativeImage> animatedCape) {
        //logger.info("Setting animated cape for {}", this.playerUUID);
        this.animatedCape = animatedCape;
        this.setHasStaticCape(false);
        this.setHasAnimatedCape(true);
        this.loadFramesToResource();
    }

    private void loadFramesToResource() {
        //logger.info("Loading resources to memory for {}", this.playerUUID);
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

