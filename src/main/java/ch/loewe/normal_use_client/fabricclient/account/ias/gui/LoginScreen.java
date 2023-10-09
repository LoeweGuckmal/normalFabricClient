package ch.loewe.normal_use_client.fabricclient.account.ias.gui;
//
import ch.loewe.normal_use_client.fabricclient.account.MicrosoftAuthCallback;
import ch.loewe.normal_use_client.fabricclient.account.SharedIAS;
import ch.loewe.normal_use_client.fabricclient.account.account.Account;
import ch.loewe.normal_use_client.fabricclient.account.account.Auth;
import ch.loewe.normal_use_client.fabricclient.account.account.OfflineAccount;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.util.function.Consumer;

public class LoginScreen extends Screen {
    private final Screen prev;
    private final Text buttonText;
    private final Text buttonTip;
    private final Consumer<Account> handler;
    private final MicrosoftAuthCallback callback = new MicrosoftAuthCallback();
    private TextFieldWidget username;
    private ButtonWidget offline;
    private ButtonWidget microsoft;
    private String state;

    public LoginScreen(Screen prev, Text title, Text buttonText, Text buttonTip, Consumer<Account> handler) {
        super(title);
        this.prev = prev;
        this.buttonText = buttonText;
        this.buttonTip = buttonTip;
        this.handler = handler;
    }

    @Override
    public void init() {
        super.init();
        addDrawableChild(offline = ButtonWidget.builder(buttonText, btn -> loginOffline()).dimensions(width / 2 - 152, this.height - 28, 150, 20).tooltip(Tooltip.of(buttonTip)).build());
        offline.active = false;
        addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, btn -> client.setScreen(prev)).dimensions(this.width / 2 + 2, this.height - 28, 150, 20).build());
        username = addDrawableChild(new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 - 12, 200, 20, username, Text.translatable("ias.loginGui.nickname")));
        username.setMaxLength(16);
        addDrawableChild(microsoft = ButtonWidget.builder(Text.translatable("ias.loginGui.microsoft"), btn -> loginMicrosoft()).dimensions(this.width / 2 - 50, this.height / 2 + 12, 100, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        renderBackground(ctx, mx, my, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, this.title, this.width / 2, 5, -1);
        ctx.drawCenteredTextWithShadow(textRenderer, I18n.translate("ias.loginGui.nickname"), this.width / 2, height / 2 - 22, -1);
        if (state != null) {
            ctx.drawCenteredTextWithShadow(textRenderer, state, width / 2, height / 3 * 2, 0xFFFF9900);
            ctx.drawCenteredTextWithShadow(textRenderer, SharedIAS.LOADING[(int) ((System.currentTimeMillis() / 50) % SharedIAS.LOADING.length)], width / 2, height / 3 * 2 + 10, 0xFFFF9900);
        }
        super.render(ctx, mx, my, delta);
    }

    @Override
    public void close() {
        client.setScreen(prev);
    }

    @Override
    public void removed() {
        SharedIAS.EXECUTOR.execute(callback::close);
        super.removed();
    }

    @Override
    public void tick() {
        offline.active = !username.getText().trim().isEmpty() && state == null;
        username.active = state == null;
        microsoft.active = state == null;
        super.tick();
    }

    private void loginMicrosoft() {
        state = "";
        SharedIAS.EXECUTOR.execute(() -> {
            state = I18n.translate("ias.loginGui.microsoft.checkBrowser");
            Util.getOperatingSystem().open(MicrosoftAuthCallback.MICROSOFT_AUTH_URL);
            callback.start((s, o) -> state = I18n.translate(s, o), I18n.translate("ias.loginGui.microsoft.canClose")).whenComplete((acc, t) -> {
                if (client.currentScreen != this) return;
                if (t != null) {
                    client.execute(() -> client.setScreen(new NoticeScreen(() -> client.setScreen(prev),
                            Text.translatable("ias.error").formatted(Formatting.RED),
                            Text.literal(String.valueOf(t)))));
                    return;
                }
                if (acc == null) {
                    client.execute(() -> client.setScreen(prev));
                    return;
                }
                client.execute(() -> {
                    handler.accept(acc);
                    client.setScreen(prev);
                });
            });
        });
    }

    private void loginOffline() {
        state = "";
        SharedIAS.EXECUTOR.execute(() -> {
            state = I18n.translate("ias.loginGui.offline.progress");
            Account account = new OfflineAccount(username.getText(), Auth.resolveUUID(username.getText()));
            client.execute(() -> {
                handler.accept(account);
                client.setScreen(prev);
            });
        });
    }
}