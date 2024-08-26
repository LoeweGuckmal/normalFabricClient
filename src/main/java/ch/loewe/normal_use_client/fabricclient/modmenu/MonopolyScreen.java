package ch.loewe.normal_use_client.fabricclient.modmenu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.function.Supplier;

import static ch.loewe.normal_use_client.fabricclient.client.FabricClientClient.logger;
import static ch.loewe.normal_use_client.fabricclient.client.FabricClientClient.settingsKeyBinding;

public class MonopolyScreen extends GameOptionsScreen {
    private final Screen parent;
    private static final Text title = Text.translatable("loewe.screen.monopoly");
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Supplier<SimpleOption<?>[]> array = ModMenuButtons::asOptionsMonopoly;
    public static int openTimeout = 0;
    public static String exeAfterClose = "";

    public MonopolyScreen(Screen parent) {
        super(parent, client.options, title);
        this.parent = parent;
    }

    @Override
    protected void addOptions() {
        assert this.body != null;
        this.body.addAll(ModMenuButtons.asOptionsMonopoly());
    }

    //DONE Button
    protected void initFooter() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
            for(int i = 0; i < array.get().length; ++i) {
                String key = ModMenuButtons.getButtonAddresses(i);
                String value = array.get()[i].getValue().toString().toLowerCase();
                if (Config.getDebug())
                    logger.info("{}: {}", key.toUpperCase(), value.toUpperCase());
            }
            client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (settingsKeyBinding.matchesKey(keyCode, scanCode)) {
            openTimeout = 5;
            close();
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
