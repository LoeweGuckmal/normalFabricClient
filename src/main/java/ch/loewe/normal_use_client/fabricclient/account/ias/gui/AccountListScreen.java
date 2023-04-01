package ch.loewe.normal_use_client.fabricclient.account.ias.gui;

import ch.loewe.normal_use_client.fabricclient.account.Config;
import ch.loewe.normal_use_client.fabricclient.account.account.Account;
import ch.loewe.normal_use_client.fabricclient.account.ias.IAS;
import ch.loewe.normal_use_client.fabricclient.mixin.MinecraftAccessor;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.report.ReporterEnvironment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.ProfileKeysImpl;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class AccountListScreen extends Screen {
    private static long nextSkinUpdate = System.currentTimeMillis();
    private final Screen prev;
    private AccountList list;
    private ButtonWidget add;
    private ButtonWidget login;
    private ButtonWidget loginOffline;
    private ButtonWidget delete;
    private ButtonWidget edit;
    private ButtonWidget reloadSkins;
    private TextFieldWidget search;
    private String state;

    public AccountListScreen(Screen prev) {
        super(Text.literal("In-Game Account Switcher"));
        this.prev = prev;
    }

    public void init() {
        this.list = new AccountList(this.client, this.width, this.height);
        this.addDrawableChild(this.list);
        this.addDrawableChild(this.reloadSkins = ButtonWidget.builder(Text.translatable("ias.listGui.reloadSkins"), (btn) -> {
            this.reloadSkins();
        }).dimensions(2, 2, 120, 20).build());
        this.addDrawableChild(this.search = new TextFieldWidget(this.textRenderer, this.width / 2 - 80, 14, 160, 16, this.search, Text.translatable("ias.listGui.search")));
        this.addDrawableChild(this.add = ButtonWidget.builder(Text.translatable("ias.listGui.add"), (btn) -> {
            this.add();
        }).dimensions(this.width / 2 + 4 + 40, this.height - 52, 120, 20).build());
        this.addDrawableChild(this.login = ButtonWidget.builder(Text.translatable("ias.listGui.login"), (btn) -> {
            this.login();
        }).dimensions(this.width / 2 - 154 - 10, this.height - 52, 120, 20).build());
        this.addDrawableChild(this.loginOffline = ButtonWidget.builder(Text.translatable("ias.listGui.loginOffline"), (btn) -> {
            this.loginOffline();
        }).dimensions(this.width / 2 - 154 - 10, this.height - 28, 110, 20).build());
        this.addDrawableChild(this.edit = ButtonWidget.builder(Text.translatable("ias.listGui.edit"), (btn) -> {
            this.edit();
        }).dimensions(this.width / 2 - 40, this.height - 52, 80, 20).build());
        this.addDrawableChild(this.delete = ButtonWidget.builder(Text.translatable("ias.listGui.delete"), (btn) -> {
            this.delete();
        }).dimensions(this.width / 2 - 50, this.height - 28, 100, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, (btn) -> {
            this.client.setScreen(this.prev);
        }).dimensions(this.width / 2 + 4 + 50, this.height - 28, 110, 20).build());
        this.updateButtons();
        this.search.setSuggestion(I18n.translate("ias.listGui.search", new Object[0]));
        this.search.setChangedListener((s) -> {
            this.list.updateAccounts(s);
            this.search.setSuggestion(s.isEmpty() ? I18n.translate("ias.listGui.search", new Object[0]) : "");
        });
        this.list.updateAccounts(this.search.getText());
    }

    public void tick() {
        this.search.tick();
        this.updateButtons();
    }

    public void removed() {
        Config.save(this.client.runDirectory.toPath());
    }

    public void render(MatrixStack ms, int mx, int my, float delta) {
        this.renderBackground(ms);
        super.render(ms, mx, my, delta);
        drawCenteredTextWithShadow(ms, this.textRenderer, this.title, this.width / 2, 4, -1);
        if (this.list.getSelectedOrNull() != null) {
            RenderSystem.setShaderTexture(0, ((AccountList.AccountEntry)this.list.getSelectedOrNull()).skin());
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            boolean slim = ((AccountList.AccountEntry)this.list.getSelectedOrNull()).slimSkin();
            ms.push();
            ms.scale(4.0F, 4.0F, 4.0F);
            ms.translate(1.0D, (double)this.height / 8.0D - 16.0D - 4.0D, 0.0D);
            Screen.drawTexture(ms, 4, 0, 8.0F, 8.0F, 8, 8, 64, 64);
            Screen.drawTexture(ms, 4, 8, 20.0F, 20.0F, 8, 12, 64, 64);
            Screen.drawTexture(ms, slim ? 1 : 0, 8, 44.0F, 20.0F, slim ? 3 : 4, 12, 64, 64);
            Screen.drawTexture(ms, 12, 8, 36.0F, 52.0F, slim ? 3 : 4, 12, 64, 64);
            Screen.drawTexture(ms, 4, 20, 4.0F, 20.0F, 4, 12, 64, 64);
            Screen.drawTexture(ms, 8, 20, 20.0F, 52.0F, 4, 12, 64, 64);
            if (this.client.options.isPlayerModelPartEnabled(PlayerModelPart.HAT)) {
                Screen.drawTexture(ms, 4, 0, 40.0F, 8.0F, 8, 8, 64, 64);
            }

            if (this.client.options.isPlayerModelPartEnabled(PlayerModelPart.RIGHT_SLEEVE)) {
                Screen.drawTexture(ms, slim ? 1 : 0, 8, 44.0F, 36.0F, slim ? 3 : 4, 12, 64, 64);
            }

            if (this.client.options.isPlayerModelPartEnabled(PlayerModelPart.LEFT_SLEEVE)) {
                Screen.drawTexture(ms, 12, 8, 52.0F, 52.0F, slim ? 3 : 4, 12, 64, 64);
            }

            if (this.client.options.isPlayerModelPartEnabled(PlayerModelPart.RIGHT_PANTS_LEG)) {
                Screen.drawTexture(ms, 4, 20, 4.0F, 36.0F, 4, 12, 64, 64);
            }

            if (this.client.options.isPlayerModelPartEnabled(PlayerModelPart.LEFT_PANTS_LEG)) {
                Screen.drawTexture(ms, 8, 20, 4.0F, 52.0F, 4, 12, 64, 64);
            }

            ms.pop();
        }

        if (this.state != null) {
            drawCenteredTextWithShadow(ms, this.textRenderer, this.state, this.width / 2, this.height - 62, -26368);
        }

    }

    private void reloadSkins() {
        if (!this.list.children().isEmpty() && System.currentTimeMillis() > nextSkinUpdate && this.state == null) {
            IAS.SKIN_CACHE.clear();
            this.list.updateAccounts(this.search.getText());
            nextSkinUpdate = System.currentTimeMillis() + 15000L;
        }
    }

    private void login() {
        if (this.list.getSelectedOrNull() != null && this.state == null) {
            Account acc = this.list.getSelectedOrNull().account();
            this.updateButtons();
            this.state = "";
            acc.login((s, o) -> this.state = I18n.translate(s, o)).whenComplete((d, t) -> {
                this.state = null;
                if (t != null) {
                    this.client.execute(() -> {
                        this.client.setScreen(new NoticeScreen(() -> {
                            this.client.setScreen(this);
                        }, Text.translatable("ias.error").formatted(Formatting.RED), Text.literal(t + "\nPlease delete the acc and add it a second time.")));
                    });
                } else {
                    this.client.execute(() -> {
                        ((MinecraftAccessor)this.client).ias$user(new Session(d.name(), UUIDTypeAdapter.fromUUID(d.uuid()), d.accessToken(), Optional.empty(), Optional.empty(), Session.AccountType.byName(d.userType())));
                        UserApiService apiSvc = ((MinecraftAccessor)this.client).ias$createUserApiService(((MinecraftAccessor)this.client).ias$authenticationService(), new RunArgs(new RunArgs.Network(this.client.getSession(), null, null, null), null, null, null, null));
                        ((MinecraftAccessor)this.client).ias$userApiService(apiSvc);
                        ((MinecraftAccessor)this.client).ias$playerSocialManager(new SocialInteractionsManager(this.client, apiSvc));
                        ((MinecraftAccessor)this.client).ias$profileKeyPairManager(new ProfileKeysImpl(apiSvc, d.uuid(), this.client.runDirectory.toPath()));
                        ((MinecraftAccessor)this.client).ias$reportingContext(AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), apiSvc));
                    });
                }
            });

        }
    }
    public void loginOffline() {
        try {
            Account acc = this.list.getSelectedOrNull().account();
            if (this.list.getSelectedOrNull() != null && this.state == null) {
                ((MinecraftAccessor) this.client).ias$user(new Session(acc.name(), UUIDTypeAdapter.fromUUID(UUID.nameUUIDFromBytes("OfflinePlayer".concat(acc.name()).getBytes(StandardCharsets.UTF_8))), "0", Optional.empty(), Optional.empty(), Session.AccountType.LEGACY));
                UserApiService apiSvc = ((MinecraftAccessor) this.client).ias$createUserApiService(((MinecraftAccessor) this.client).ias$authenticationService(), new RunArgs(new RunArgs.Network(this.client.getSession(), null, null, null), null, null, null, null));
                ((MinecraftAccessor) this.client).ias$userApiService(apiSvc);
                ((MinecraftAccessor) this.client).ias$playerSocialManager(new SocialInteractionsManager(this.client, apiSvc));
                ((MinecraftAccessor) this.client).ias$profileKeyPairManager(new ProfileKeysImpl(apiSvc, new UUID(0L, 0L), this.client.runDirectory.toPath()));
                ((MinecraftAccessor) this.client).ias$reportingContext(AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), apiSvc));
            }
        } catch (Exception ignored){}
    }
    public void loginOffline(Account acc) {
        try {
            ((MinecraftAccessor) this.client).ias$user(new Session(acc.name(), UUIDTypeAdapter.fromUUID(UUID.nameUUIDFromBytes("OfflinePlayer".concat(acc.name()).getBytes(StandardCharsets.UTF_8))), "0", Optional.empty(), Optional.empty(), Session.AccountType.LEGACY));
            UserApiService apiSvc = ((MinecraftAccessor) this.client).ias$createUserApiService(((MinecraftAccessor) this.client).ias$authenticationService(), new RunArgs(new RunArgs.Network(this.client.getSession(), null, null, null), null, null, null, null));
            ((MinecraftAccessor) this.client).ias$userApiService(apiSvc);
            ((MinecraftAccessor) this.client).ias$playerSocialManager(new SocialInteractionsManager(this.client, apiSvc));
            ((MinecraftAccessor) this.client).ias$profileKeyPairManager(new ProfileKeysImpl(apiSvc, new UUID(0L, 0L), this.client.runDirectory.toPath()));
            ((MinecraftAccessor) this.client).ias$reportingContext(AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), apiSvc));
        } catch (Exception ignored){}
    }

    private void add() {
        if (this.state == null) {
            this.client.setScreen(new LoginScreen(this, Text.translatable("ias.loginGui.add"), Text.translatable("ias.loginGui.add.button"), Text.translatable("ias.loginGui.add.button.tooltip"), (acc) -> {
                Config.accounts.add(acc);
                Config.save(this.client.runDirectory.toPath());
                this.list.updateAccounts(this.search.getText());
            }));
        }
    }

    public void edit() {
        if (this.list.getSelectedOrNull() != null && this.state == null) {
            Account acc = this.list.getSelectedOrNull().account();
            this.client.setScreen(new LoginScreen(this, Text.translatable("ias.loginGui.edit"), Text.translatable("ias.loginGui.edit.button"), Text.translatable("ias.loginGui.edit.button.tooltip"), (newAcc) -> {
                Config.accounts.set(Config.accounts.indexOf(acc), newAcc);
                Config.save(this.client.runDirectory.toPath());
            }));
        }
    }

    public void delete() {
        if (this.list.getSelectedOrNull() != null && this.state == null) {
            Account acc = this.list.getSelectedOrNull().account();
            if (hasShiftDown()) {
                Config.accounts.remove(acc);
                Config.save(this.client.runDirectory.toPath());
                this.updateButtons();
                this.list.updateAccounts(this.search.getText());
            } else {
                this.client.setScreen(new ConfirmScreen((b) -> {
                    if (b) {
                        Config.accounts.remove(acc);
                        this.updateButtons();
                        this.list.updateAccounts(this.search.getText());
                    }

                    this.client.setScreen(this);
                }, Text.translatable("ias.deleteGui.title"), Text.translatable("ias.deleteGui.text", new Object[]{acc.name()})));
            }
        }
    }

    private void updateButtons() {
        this.login.active = this.list.getSelectedOrNull() != null && this.state == null;
        this.loginOffline.active = this.list.getSelectedOrNull() != null;
        this.add.active = this.state == null;
        this.edit.active = this.list.getSelectedOrNull() != null && this.state == null;
        this.delete.active = this.list.getSelectedOrNull() != null && this.state == null;
        this.reloadSkins.active = this.list.getSelectedOrNull() != null && this.state == null && System.currentTimeMillis() > nextSkinUpdate;
    }

    public boolean keyPressed(int key, int scan, int mods) {
        if (this.search.isFocused()) {
            return super.keyPressed(key, scan, mods);
        } else if (key != 294 && key != 82) {
            if (key != 257 && key != 335) {
                if (key != 65 && key != 61 && key != 334) {
                    if (key != 46 && key != 331) {
                        if (key != 261 && key != 45 && key != 333) {
                            return super.keyPressed(key, scan, mods);
                        } else {
                            this.delete();
                            return true;
                        }
                    } else {
                        this.edit();
                        return true;
                    }
                } else {
                    this.add();
                    return true;
                }
            } else {
                if (Screen.hasShiftDown()) {
                    this.loginOffline();
                } else {
                    this.login();
                }

                return true;
            }
        } else {
            this.reloadSkins();
            return true;
        }
    }

    public void close() {
        this.client.setScreen(this.prev);
    }
}
