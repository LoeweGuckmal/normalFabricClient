package ch.loewe.normal_use_client.fabricclient.account.gui;
//
import ch.loewe.normal_use_client.fabricclient.account.Config;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.Objects;

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

    @Override
    public void init() {
        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> client.setScreen(prev)).dimensions(width / 2 - 75, height - 28, 150, 20).build());
        //addDrawableChild(titleScreenText = new CheckboxWidget(5, 20, 24 + textRenderer.getWidth(Text.translatable(
        //        "ias.configGui.titleScreenText")), 20, Text.translatable("ias.configGui.titleScreenText"), Config.titleScreenText));
        addDrawableChild(titleScreenTextX = new TextFieldWidget(textRenderer, 35 + textRenderer.getWidth(Text.translatable(
                "ias.configGui.titleScreenText")), 20, 50, 20, Text.literal("X")));
        addDrawableChild(titleScreenTextY = new TextFieldWidget(textRenderer, 35 + textRenderer.getWidth(Text.translatable(
                "ias.configGui.titleScreenText")) + 54, 20, 50, 20, Text.literal("Y")));
        addDrawableChild(titleScreenTextAlignment = ButtonWidget.builder(
                Text.translatable("ias.configGui.titleScreenText.alignment", I18n.translate(Config.titleScreenTextAlignment.key())),
                btn -> changeAlignment()).dimensions(
                35 + textRenderer.getWidth(Text.translatable(
                        "ias.configGui.titleScreenText")) + 108, 20, textRenderer.getWidth(Text.translatable(
                        "ias.configGui.titleScreenText.alignment", I18n.translate(Config.titleScreenTextAlignment.key()))) + 20, 20
        ).build());
        //addDrawableChild(titleScreenButton = new CheckboxWidget(5, 44, 24 + textRenderer.getWidth(Text.translatable(
        //        "ias.configGui.titleScreenButton")), 20, Text.translatable(
        //        "ias.configGui.titleScreenButton"), Config.titleScreenButton));
        addDrawableChild(titleScreenButtonX = new TextFieldWidget(textRenderer, 35 + textRenderer.getWidth(Text.translatable(
                "ias.configGui.titleScreenButton")), 44, 50, 20, Text.literal("X")));
        addDrawableChild(titleScreenButtonY = new TextFieldWidget(textRenderer, 35 + textRenderer.getWidth(Text.translatable(
                "ias.configGui.titleScreenButton")) + 54, 44, 50, 20, Text.literal("Y")));
        //addDrawableChild(multiplayerScreenButton = new CheckboxWidget(5, 68, 24 + textRenderer.getWidth(Text.translatable(
        //        "ias.configGui.multiplayerScreenButton")), 20, Text.translatable(
        //        "ias.configGui.multiplayerScreenButton"), Config.multiplayerScreenButton));
        addDrawableChild(multiplayerScreenButtonX = new TextFieldWidget(textRenderer, 35 + textRenderer.getWidth(Text.translatable(
                "ias.configGui.multiplayerScreenButton")), 68, 50, 20, Text.literal("X")));
        addDrawableChild(multiplayerScreenButtonY = new TextFieldWidget(textRenderer, 35 + textRenderer.getWidth(Text.translatable(
                "ias.configGui.multiplayerScreenButton")) + 54, 68, 50, 20, Text.literal("Y")));
        titleScreenTextX.setSuggestion(titleScreenTextX.getText().isEmpty() ? "X" : "");
        titleScreenTextY.setSuggestion(titleScreenTextY.getText().isEmpty() ? "Y" : "");
        titleScreenButtonX.setSuggestion(titleScreenButtonX.getText().isEmpty() ? "X" : "");
        titleScreenButtonY.setSuggestion(titleScreenButtonY.getText().isEmpty() ? "Y" : "");
        multiplayerScreenButtonX.setSuggestion(multiplayerScreenButtonX.getText().isEmpty() ? "X" : "");
        multiplayerScreenButtonY.setSuggestion(multiplayerScreenButtonY.getText().isEmpty() ? "Y" : "");
        titleScreenTextX.setChangedListener(s -> titleScreenTextX.setSuggestion(s.isEmpty() ? "X" : ""));
        titleScreenTextY.setChangedListener(s -> titleScreenTextY.setSuggestion(s.isEmpty() ? "Y" : ""));
        titleScreenButtonX.setChangedListener(s -> titleScreenButtonX.setSuggestion(s.isEmpty() ? "X" : ""));
        titleScreenButtonY.setChangedListener(s -> titleScreenButtonY.setSuggestion(s.isEmpty() ? "Y" : ""));
        multiplayerScreenButtonX.setChangedListener(s -> multiplayerScreenButtonX.setSuggestion(s.isEmpty() ? "X" : ""));
        multiplayerScreenButtonY.setChangedListener(s -> multiplayerScreenButtonY.setSuggestion(s.isEmpty() ? "Y" : ""));
        titleScreenTextX.setText(Objects.toString(Config.titleScreenTextX, ""));
        titleScreenTextY.setText(Objects.toString(Config.titleScreenTextY, ""));
        titleScreenButtonX.setText(Objects.toString(Config.titleScreenButtonX, ""));
        titleScreenButtonY.setText(Objects.toString(Config.titleScreenButtonY, ""));
        multiplayerScreenButtonX.setText(Objects.toString(Config.multiplayerScreenButtonX, ""));
        multiplayerScreenButtonY.setText(Objects.toString(Config.multiplayerScreenButtonY, ""));
        tick();
    }

    private void changeAlignment() {
        int i = Config.titleScreenTextAlignment.ordinal() + 1;
        if (i >= Config.Alignment.values().length) i = 0;
        Config.titleScreenTextAlignment = Config.Alignment.values()[i];
        titleScreenTextAlignment.setMessage(Text.translatable("ias.configGui.titleScreenText.alignment",
                I18n.translate(Config.titleScreenTextAlignment.key())));
        titleScreenTextAlignment.setWidth(textRenderer.getWidth(titleScreenTextAlignment.getMessage()) + 20);
    }

    @Override
    public void close() {
        client.setScreen(prev);
    }

    @Override
    public void removed() {
        Config.titleScreenText = titleScreenText.isChecked();
        Config.titleScreenTextX = titleScreenTextX.getText().trim().isEmpty() ? null : titleScreenTextX.getText();
        Config.titleScreenTextY = titleScreenTextY.getText().trim().isEmpty() ? null : titleScreenTextY.getText();
        Config.titleScreenButton = titleScreenButton.isChecked();
        Config.titleScreenButtonX = titleScreenButtonX.getText().trim().isEmpty() ? null : titleScreenButtonX.getText();
        Config.titleScreenButtonY = titleScreenButtonY.getText().trim().isEmpty() ? null : titleScreenButtonY.getText();
        Config.multiplayerScreenButton = multiplayerScreenButton.isChecked();
        Config.multiplayerScreenButtonX = multiplayerScreenButtonX.getText().trim().isEmpty() ? null : multiplayerScreenButtonX.getText();
        Config.multiplayerScreenButtonY = multiplayerScreenButtonY.getText().trim().isEmpty() ? null : multiplayerScreenButtonY.getText();
        Config.save(client.runDirectory.toPath());
    }

    @Override
    public void tick() {
        titleScreenTextX.visible = titleScreenTextY.visible = titleScreenTextAlignment.visible = titleScreenText.isChecked();
        titleScreenButtonX.visible = titleScreenButtonY.visible = titleScreenButton.isChecked();
        multiplayerScreenButtonX.visible = multiplayerScreenButtonY.visible = multiplayerScreenButton.isChecked();
        super.tick();
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        renderBackground(ctx, mx, my, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, this.title, width / 2, 5, -1);
        super.render(ctx, mx, my, delta);
    }
}