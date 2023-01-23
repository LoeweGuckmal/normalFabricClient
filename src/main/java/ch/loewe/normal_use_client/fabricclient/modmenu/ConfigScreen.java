package ch.loewe.normal_use_client.fabricclient.modmenu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.SimpleOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class ConfigScreen extends SimpleOptionsScreen {
    private final Screen parent;
    private static final Text title = Text.translatable("fastload.screen.config");
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Supplier<SimpleOption<?>[]> array = ModMenuButtons::asOptions;

    public ConfigScreen(Screen parent) {
        super(parent, client.options, title, ModMenuButtons.asOptions());
        this.parent = parent;
    }

    protected void initFooter() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
            for(int i = 0; i < array.get().length; ++i) {
                String key = ModMenuButtons.getButtonAddresses(i);
                String value = ((SimpleOption[])array.get())[i].getValue().toString().toLowerCase();
                if (FLMath.getDebug()) {
                    Logger var10000 = LoggerFactory.getLogger("Fastload");;
                    String var10001 = key.toUpperCase();
                    var10000.info(var10001 + ": " + value.toUpperCase());
                }

                FLConfig.storeProperty(key, value);
            }

            FLConfig.writeToDisk();
            client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
