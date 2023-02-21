package ch.loewe.normal_use_client.fabricclient.modmenu;

import ch.loewe.normal_use_client.fabricclient.cape.DownloadManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.SimpleOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

import static ch.loewe.normal_use_client.fabricclient.client.FabricClientClient.*;

public class ConfigScreen extends SimpleOptionsScreen {
    private final Screen parent;
    private static final Text title = Text.translatable("loewe.screen.config");
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Supplier<SimpleOption<?>[]> array = ModMenuButtons::asOptions;
    public static int openTimeout = 0;

    public ConfigScreen(Screen parent) {
        super(parent, client.options, title, ModMenuButtons.asOptions());
        this.parent = parent;
    }

    //DONE Button
    protected void initFooter() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
            for(int i = 0; i < array.get().length; ++i) {
                String key = ModMenuButtons.getButtonAddresses(i);
                String value = array.get()[i].getValue().toString().toLowerCase();
                if (Config.getDebug())
                    logger.info(key.toUpperCase() + ": " + value.toUpperCase());
            }
            client.setScreen(this.parent);
            if (mc.player != null) {
                DownloadManager.prepareDownload(mc.player, true, false);
            }
        }).dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        super.render(matrices, mouseX, mouseY, delta);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (settingsKeyBinding.matchesKey(keyCode, scanCode)) {
            openTimeout = 5;
            close();
        }
        if (infoKeyBinding.matchesKey(keyCode, scanCode)) {
            logger.info(String.valueOf(mc.player.getServer() != null));
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
