package ch.loewe.normal_use_client.fabricclient.account.gui;

import java.util.Objects;

import ch.loewe.normal_use_client.fabricclient.account.Config;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;

public class IASConfigScreen extends Screen {
    private final Screen prev;
    private CheckboxWidget titleScreenText;
    private TextFieldWidget titleScreenTextX;
    private TextFieldWidget titleScreenTextY;
    private ButtonWidget titleScreenTextAlignment;
    private CheckboxWidget titleScreenButton;
    private TextFieldWidget titleScreenButtonX;
    private TextFieldWidget titleScreenButtonY;
    private CheckboxWidget multiplayerScreenButton;
    private TextFieldWidget multiplayerScreenButtonX;
    private TextFieldWidget multiplayerScreenButtonY;

    public IASConfigScreen(Screen prev) {
        super(Text.translatable("ias.configGui.title"));
        this.prev = prev;
    }

    public void init() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
            this.client.setScreen(this.prev);
        }).dimensions(this.width / 2 - 75, this.height - 28, 150, 20).build());
        this.addDrawableChild(this.titleScreenText = new CheckboxWidget(5, 20, 24 + this.textRenderer.getWidth(Text.translatable("ias.configGui.titleScreenText")), 20, Text.translatable("ias.configGui.titleScreenText"), Config.titleScreenText));
        this.addDrawableChild(this.titleScreenTextX = new TextFieldWidget(this.textRenderer, 35 + this.textRenderer.getWidth(Text.translatable("ias.configGui.titleScreenText")), 20, 50, 20, Text.literal("X")));
        this.addDrawableChild(this.titleScreenTextY = new TextFieldWidget(this.textRenderer, 35 + this.textRenderer.getWidth(Text.translatable("ias.configGui.titleScreenText")) + 54, 20, 50, 20, Text.literal("Y")));
        this.addDrawableChild(this.titleScreenTextAlignment = ButtonWidget.builder(Text.translatable("ias.configGui.titleScreenText.alignment", new Object[]{I18n.translate(Config.titleScreenTextAlignment.key(), new Object[0])}), (btn) -> {
            this.changeAlignment();
        }).dimensions(35 + this.textRenderer.getWidth(Text.translatable("ias.configGui.titleScreenText")) + 108, 20, this.textRenderer.getWidth(Text.translatable("ias.configGui.titleScreenText.alignment", new Object[]{I18n.translate(Config.titleScreenTextAlignment.key(), new Object[0])})) + 20, 20).build());
        this.addDrawableChild(this.titleScreenButton = new CheckboxWidget(5, 44, 24 + this.textRenderer.getWidth(Text.translatable("ias.configGui.titleScreenButton")), 20, Text.translatable("ias.configGui.titleScreenButton"), Config.titleScreenButton));
        this.addDrawableChild(this.titleScreenButtonX = new TextFieldWidget(this.textRenderer, 35 + this.textRenderer.getWidth(Text.translatable("ias.configGui.titleScreenButton")), 44, 50, 20, Text.literal("X")));
        this.addDrawableChild(this.titleScreenButtonY = new TextFieldWidget(this.textRenderer, 35 + this.textRenderer.getWidth(Text.translatable("ias.configGui.titleScreenButton")) + 54, 44, 50, 20, Text.literal("Y")));
        this.addDrawableChild(this.multiplayerScreenButton = new CheckboxWidget(5, 68, 24 + this.textRenderer.getWidth(Text.translatable("ias.configGui.multiplayerScreenButton")), 20, Text.translatable("ias.configGui.multiplayerScreenButton"), Config.multiplayerScreenButton));
        this.addDrawableChild(this.multiplayerScreenButtonX = new TextFieldWidget(this.textRenderer, 35 + this.textRenderer.getWidth(Text.translatable("ias.configGui.multiplayerScreenButton")), 68, 50, 20, Text.literal("X")));
        this.addDrawableChild(this.multiplayerScreenButtonY = new TextFieldWidget(this.textRenderer, 35 + this.textRenderer.getWidth(Text.translatable("ias.configGui.multiplayerScreenButton")) + 54, 68, 50, 20, Text.literal("Y")));
        this.titleScreenTextX.setSuggestion(this.titleScreenTextX.getText().isEmpty() ? "X" : "");
        this.titleScreenTextY.setSuggestion(this.titleScreenTextY.getText().isEmpty() ? "Y" : "");
        this.titleScreenButtonX.setSuggestion(this.titleScreenButtonX.getText().isEmpty() ? "X" : "");
        this.titleScreenButtonY.setSuggestion(this.titleScreenButtonY.getText().isEmpty() ? "Y" : "");
        this.multiplayerScreenButtonX.setSuggestion(this.multiplayerScreenButtonX.getText().isEmpty() ? "X" : "");
        this.multiplayerScreenButtonY.setSuggestion(this.multiplayerScreenButtonY.getText().isEmpty() ? "Y" : "");
        this.titleScreenTextX.setChangedListener((s) -> {
            this.titleScreenTextX.setSuggestion(s.isEmpty() ? "X" : "");
        });
        this.titleScreenTextY.setChangedListener((s) -> {
            this.titleScreenTextY.setSuggestion(s.isEmpty() ? "Y" : "");
        });
        this.titleScreenButtonX.setChangedListener((s) -> {
            this.titleScreenButtonX.setSuggestion(s.isEmpty() ? "X" : "");
        });
        this.titleScreenButtonY.setChangedListener((s) -> {
            this.titleScreenButtonY.setSuggestion(s.isEmpty() ? "Y" : "");
        });
        this.multiplayerScreenButtonX.setChangedListener((s) -> {
            this.multiplayerScreenButtonX.setSuggestion(s.isEmpty() ? "X" : "");
        });
        this.multiplayerScreenButtonY.setChangedListener((s) -> {
            this.multiplayerScreenButtonY.setSuggestion(s.isEmpty() ? "Y" : "");
        });
        this.titleScreenTextX.setText(Objects.toString(Config.titleScreenTextX, ""));
        this.titleScreenTextY.setText(Objects.toString(Config.titleScreenTextY, ""));
        this.titleScreenButtonX.setText(Objects.toString(Config.titleScreenButtonX, ""));
        this.titleScreenButtonY.setText(Objects.toString(Config.titleScreenButtonY, ""));
        this.multiplayerScreenButtonX.setText(Objects.toString(Config.multiplayerScreenButtonX, ""));
        this.multiplayerScreenButtonY.setText(Objects.toString(Config.multiplayerScreenButtonY, ""));
        this.tick();
    }

    private void changeAlignment() {
        int i = Config.titleScreenTextAlignment.ordinal() + 1;
        if (i >= Config.Alignment.values().length) {
            i = 0;
        }

        Config.titleScreenTextAlignment = Config.Alignment.values()[i];
        this.titleScreenTextAlignment.setMessage(Text.translatable("ias.configGui.titleScreenText.alignment", I18n.translate(Config.titleScreenTextAlignment.key(), new Object[0])));
        this.titleScreenTextAlignment.setWidth(this.textRenderer.getWidth(this.titleScreenTextAlignment.getMessage()) + 20);
    }

    public void close() {
        this.client.setScreen(this.prev);
    }

    public void removed() {
        Config.titleScreenText = this.titleScreenText.isChecked();
        Config.titleScreenTextX = this.titleScreenTextX.getText().trim().isEmpty() ? null : this.titleScreenTextX.getText();
        Config.titleScreenTextY = this.titleScreenTextY.getText().trim().isEmpty() ? null : this.titleScreenTextY.getText();
        Config.titleScreenButton = this.titleScreenButton.isChecked();
        Config.titleScreenButtonX = this.titleScreenButtonX.getText().trim().isEmpty() ? null : this.titleScreenButtonX.getText();
        Config.titleScreenButtonY = this.titleScreenButtonY.getText().trim().isEmpty() ? null : this.titleScreenButtonY.getText();
        Config.multiplayerScreenButton = this.multiplayerScreenButton.isChecked();
        Config.multiplayerScreenButtonX = this.multiplayerScreenButtonX.getText().trim().isEmpty() ? null : this.multiplayerScreenButtonX.getText();
        Config.multiplayerScreenButtonY = this.multiplayerScreenButtonY.getText().trim().isEmpty() ? null : this.multiplayerScreenButtonY.getText();
        Config.save(this.client.runDirectory.toPath());
    }

    public void tick() {
        this.titleScreenTextX.visible = this.titleScreenTextY.visible = this.titleScreenTextAlignment.visible = this.titleScreenText.isChecked();
        this.titleScreenButtonX.visible = this.titleScreenButtonY.visible = this.titleScreenButton.isChecked();
        this.multiplayerScreenButtonX.visible = this.multiplayerScreenButtonY.visible = this.multiplayerScreenButton.isChecked();
        this.titleScreenTextX.tick();
        this.titleScreenTextY.tick();
        this.titleScreenButtonX.tick();
        this.titleScreenButtonY.tick();
        this.multiplayerScreenButtonX.tick();
        this.multiplayerScreenButtonY.tick();
        super.tick();
    }

    public void render(MatrixStack ms, int mx, int my, float delta) {
        this.renderBackground(ms);
        drawCenteredTextWithShadow(ms, this.textRenderer, this.title, this.width / 2, 5, -1);
        super.render(ms, mx, my, delta);
    }
}
