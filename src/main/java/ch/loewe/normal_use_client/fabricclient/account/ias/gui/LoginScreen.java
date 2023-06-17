package ch.loewe.normal_use_client.fabricclient.account.ias.gui;

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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.util.function.Consumer;

import java.util.Objects;
import java.util.concurrent.Executor;

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

    public void init() {
        super.init();
        this.addDrawableChild(this.offline = ButtonWidget.builder(this.buttonText, (btn) -> {
            this.loginOffline();
        }).dimensions(this.width / 2 - 152, this.height - 28, 150, 20).tooltip(Tooltip.of(this.buttonTip)).build());
        this.offline.active = false;
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, (btn) -> {
            this.client.setScreen(this.prev);
        }).dimensions(this.width / 2 + 2, this.height - 28, 150, 20).build());
        this.username = this.addDrawableChild(new TextFieldWidget(this.textRenderer, this.width / 2 - 100, this.height / 2 - 12, 200, 20, this.username, Text.translatable("ias.loginGui.nickname")));
        this.username.setMaxLength(16);
        this.addDrawableChild(this.microsoft = ButtonWidget.builder(Text.translatable("ias.loginGui.microsoft"), (btn) -> {
            this.loginMicrosoft();
        }).dimensions(this.width / 2 - 50, this.height / 2 + 12, 100, 20).build());
    }

    public void render(DrawContext drawContext, int mx, int my, float delta) {
        this.renderBackground(drawContext);
        drawContext.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 5, -1);
        drawContext.drawCenteredTextWithShadow(this.textRenderer, I18n.translate("ias.loginGui.nickname", new Object[0]), this.width / 2, this.height / 2 - 22, -1);
        if (this.state != null) {
            drawContext.drawCenteredTextWithShadow(this.textRenderer, this.state, this.width / 2, this.height / 3 * 2, -26368);
            drawContext.drawCenteredTextWithShadow(this.textRenderer, SharedIAS.LOADING[(int)(System.currentTimeMillis() / 50L % (long)SharedIAS.LOADING.length)], this.width / 2, this.height / 3 * 2 + 10, -26368);
        }

        super.render(drawContext, mx, my, delta);
    }

    public void close() {
        this.client.setScreen(this.prev);
    }

    public void removed() {
        Executor var10000 = SharedIAS.EXECUTOR;
        MicrosoftAuthCallback var10001 = this.callback;
        Objects.requireNonNull(var10001);
        var10000.execute(var10001::close);
        super.removed();
    }

    public void tick() {
        this.offline.active = !this.username.getText().trim().isEmpty() && this.state == null;
        this.username.active = this.state == null;
        this.microsoft.active = this.state == null;
        this.username.tick();
        super.tick();
    }

    private void loginMicrosoft() {
        this.state = "";
        SharedIAS.EXECUTOR.execute(() -> {
            this.state = I18n.translate("ias.loginGui.microsoft.checkBrowser", new Object[0]);
            //Util.getOperatingSystem().open("https://login.live.com/oauth20_authorize.srf?client_id=54fd49e4-2103-4044-9603-2b028c814ec3&response_type=code&scope=XboxLive.signin%20XboxLive.offline_access&redirect_uri=http://localhost:59125&prompt=select_account");
            Util.getOperatingSystem().open("https://login.live.com/oauth20_authorize.srf?client_id=55fca734-6e47-4719-ac3f-1fcdc5600732&response_type=code&scope=XboxLive.signin%20XboxLive.offline_access&redirect_uri=http://localhost:59125&prompt=select_account");
            this.callback.start((s, o) -> {
                this.state = I18n.translate(s, o);
            }, I18n.translate("ias.loginGui.microsoft.canClose", new Object[0])).whenComplete((acc, t) -> {
                if (this.client.currentScreen == this) {
                    if (t != null) {
                        this.client.execute(() -> {
                            this.client.setScreen(new NoticeScreen(() -> {
                                this.client.setScreen(this.prev);
                            }, Text.translatable("ias.error").formatted(Formatting.RED), Text.literal(String.valueOf(t))));
                        });
                    } else if (acc == null) {
                        this.client.execute(() -> {
                            this.client.setScreen(this.prev);
                        });
                    } else {
                        this.client.execute(() -> {
                            this.handler.accept(acc);
                            this.client.setScreen(this.prev);
                        });
                    }
                }
            });
        });
    }

    private void loginOffline() {
        this.state = "";
        SharedIAS.EXECUTOR.execute(() -> {
            this.state = I18n.translate("ias.loginGui.offline.progress");
            Account account = new OfflineAccount(this.username.getText(), Auth.resolveUUID(this.username.getText()));
            this.client.execute(() -> {
                this.handler.accept(account);
                this.client.setScreen(this.prev);
            });
        });
    }
}
