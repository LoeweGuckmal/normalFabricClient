package ch.loewe.normal_use_client.fabricclient.account.ias.gui;
//
import ch.loewe.normal_use_client.fabricclient.account.Config;
import ch.loewe.normal_use_client.fabricclient.account.account.Account;
import ch.loewe.normal_use_client.fabricclient.account.ias.IAS;
import ch.loewe.normal_use_client.fabricclient.mixin.MinecraftAccessor;
import com.mojang.authlib.minecraft.UserApiService;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.session.ProfileKeysImpl;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

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

    @Override
    public void init() {
        list = new AccountList(client, width, height);
        addDrawableChild(list);
        addDrawableChild(reloadSkins = ButtonWidget.builder(Text.translatable("ias.listGui.reloadSkins"), btn -> reloadSkins()).dimensions(2, 2, 120, 20).build());
        addDrawableChild(search = new TextFieldWidget(this.textRenderer, this.width / 2 - 80, 14, 160, 16, search, Text.translatable("ias.listGui.search")));
        addDrawableChild(add = ButtonWidget.builder(Text.translatable("ias.listGui.add"), btn -> add()).dimensions(this.width / 2 + 4 + 40, this.height - 52, 120, 20).build());
        addDrawableChild(login = ButtonWidget.builder(Text.translatable("ias.listGui.login"), btn -> login()).dimensions(this.width / 2 - 154 - 10, this.height - 52, 120, 20).build());
        addDrawableChild(loginOffline = ButtonWidget.builder(Text.translatable("ias.listGui.loginOffline"), btn -> loginOffline()).dimensions(this.width / 2 - 154 - 10, this.height - 28, 110, 20).build());
        addDrawableChild(edit = ButtonWidget.builder(Text.translatable("ias.listGui.edit"), btn -> edit()).dimensions(this.width / 2 - 40, this.height - 52, 80, 20).build());
        addDrawableChild(delete = ButtonWidget.builder(Text.translatable("ias.listGui.delete"), btn -> delete()).dimensions(this.width / 2 - 50, this.height - 28, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, btn -> client.setScreen(prev)).dimensions(this.width / 2 + 4 + 50, this.height - 28, 110, 20).build());
        updateButtons();
        search.setSuggestion(I18n.translate("ias.listGui.search"));
        search.setChangedListener(s -> {
            list.updateAccounts(s);
            search.setSuggestion(s.isEmpty() ? I18n.translate("ias.listGui.search") : "");
        });
        list.updateAccounts(search.getText());
    }

    @Override
    public void tick() {
        updateButtons();
    }

    @Override
    public void removed() {
        Config.save(client.runDirectory.toPath());
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        renderBackground(ctx, mx, my, delta);
        super.render(ctx, mx, my, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, this.title, this.width / 2, 4, -1);
        if (list.getSelectedOrNull() != null) {
            SkinTextures skin = list.getSelectedOrNull().skin();
            boolean slim = skin.model() == SkinTextures.Model.SLIM;
            Identifier skinTexture = skin.texture();
            MatrixStack ms = ctx.getMatrices();
            ms.push();
            ms.scale(4, 4, 4);
            ms.translate(1, height / 8D - 16D - 4D, 0);
            ctx.drawTexture(skinTexture, 4, 0, 8, 8, 8, 8, 64, 64); // Head
            ctx.drawTexture(skinTexture, 4, 8, 20, 20, 8, 12, 64, 64); // Body
            ctx.drawTexture(skinTexture, slim ? 1 : 0, 8, 44, 20, slim ? 3 : 4, 12, 64, 64); // Right Arm (Left from our perspective)
            ctx.drawTexture(skinTexture, 12, 8, 36, 52, slim ? 3 : 4, 12, 64, 64); // Left Arm (Right from our perspective)
            ctx.drawTexture(skinTexture, 4, 20, 4, 20, 4, 12, 64, 64); // Right Leg (Left from our perspective)
            ctx.drawTexture(skinTexture, 8, 20, 20, 52, 4, 12, 64, 64); // Left Leg (Right from our perspective)
            if (client.options.isPlayerModelPartEnabled(PlayerModelPart.HAT))
                ctx.drawTexture(skinTexture, 4, 0, 40, 8, 8, 8, 64, 64); // Head (Overlay)
            if (client.options.isPlayerModelPartEnabled(PlayerModelPart.RIGHT_SLEEVE))
                ctx.drawTexture(skinTexture, slim ? 1 : 0, 8, 44, 36, slim ? 3 : 4, 12, 64, 64); // Right Arm (Overlay)
            if (client.options.isPlayerModelPartEnabled(PlayerModelPart.LEFT_SLEEVE))
                ctx.drawTexture(skinTexture, 12, 8, 52, 52, slim ? 3 : 4, 12, 64, 64); // Left Arm (Overlay)
            if (client.options.isPlayerModelPartEnabled(PlayerModelPart.RIGHT_PANTS_LEG))
                ctx.drawTexture(skinTexture, 4, 20, 4, 36, 4, 12, 64, 64); // Right Leg (Overlay)
            if (client.options.isPlayerModelPartEnabled(PlayerModelPart.LEFT_PANTS_LEG))
                ctx.drawTexture(skinTexture, 8, 20, 4, 52, 4, 12, 64, 64); // Left Leg (Overlay)
            ms.pop();
        }
        if (state != null) {
            ctx.drawCenteredTextWithShadow(textRenderer, state, this.width / 2, this.height - 62, 0xFFFF9900);
        }
    }

    private void reloadSkins() {
        if (list.children().isEmpty() || System.currentTimeMillis() <= nextSkinUpdate || state != null) return;
        IAS.SKIN_CACHE.clear();
        list.updateAccounts(search.getText());
        nextSkinUpdate = System.currentTimeMillis() + 15000L;
    }

    private void login() {
        if (list.getSelectedOrNull() == null || state != null) return;
        Account acc = list.getSelectedOrNull().account();
        updateButtons();
        state = "";
        acc.login((s, o) -> state = I18n.translate(s, o)).whenComplete((d, t) -> {
            state = null;
            if (t != null) {
                client.execute(() -> client.setScreen(new NoticeScreen(() -> client.setScreen(this),
                        Text.translatable("ias.error").formatted(Formatting.RED),
                        Text.literal(String.valueOf(t)))));
                return;
            }
            client.execute(() -> {
                ((MinecraftAccessor) client).ias$user(new Session(d.name(), d.uuid(), d.accessToken(), Optional.empty(), Optional.empty(), Session.AccountType.byName(d.userType())));
                UserApiService apiSvc = ((MinecraftAccessor) client).ias$createUserApiService(((MinecraftAccessor) client).ias$authenticationService(), new RunArgs(new RunArgs.Network(client.getSession(), null, null, null), null, null, null, null));
                ((MinecraftAccessor) client).ias$userApiService(apiSvc);
                ((MinecraftAccessor) client).ias$playerSocialManager(new SocialInteractionsManager(client, apiSvc));
                ((MinecraftAccessor) client).ias$profileKeyPairManager(new ProfileKeysImpl(apiSvc, d.uuid(), client.runDirectory.toPath()));
                ((MinecraftAccessor) client).ias$reportingContext(AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), apiSvc));
            });
        });
    }

    private void loginOffline() {
        if (list.getSelectedOrNull() == null || state != null) return;
        Account acc = list.getSelectedOrNull().account();
        ((MinecraftAccessor) client).ias$user(new Session(acc.name(), UUID
                .nameUUIDFromBytes("OfflinePlayer".concat(acc.name()).getBytes(StandardCharsets.UTF_8)),
                "0", Optional.empty(), Optional.empty(), Session.AccountType.LEGACY));
        UserApiService apiSvc = ((MinecraftAccessor) client).ias$createUserApiService(((MinecraftAccessor) client).ias$authenticationService(), new RunArgs(new RunArgs.Network(client.getSession(), null, null, null), null, null, null, null));
        ((MinecraftAccessor) client).ias$userApiService(apiSvc);
        ((MinecraftAccessor) client).ias$playerSocialManager(new SocialInteractionsManager(client, apiSvc));
        ((MinecraftAccessor) client).ias$profileKeyPairManager(new ProfileKeysImpl(apiSvc, new UUID(0, 0), client.runDirectory.toPath()));
        ((MinecraftAccessor) client).ias$reportingContext(AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), apiSvc));
    }

    private void add() {
        if (state != null) return;
        client.setScreen(new LoginScreen(this, Text.translatable("ias.loginGui.add"),
                Text.translatable("ias.loginGui.add.button"),
                Text.translatable("ias.loginGui.add.button.tooltip"), acc -> {
            Config.accounts.add(acc);
            Config.save(client.runDirectory.toPath());
            list.updateAccounts(search.getText());
        }));
    }

    public void edit() {
        if (list.getSelectedOrNull() == null || state != null) return;
        Account acc = list.getSelectedOrNull().account();
        client.setScreen(new LoginScreen(this, Text.translatable("ias.loginGui.edit"),
                Text.translatable("ias.loginGui.edit.button"),
                Text.translatable("ias.loginGui.edit.button.tooltip"), newAcc -> {
            Config.accounts.set(Config.accounts.indexOf(acc), newAcc);
            Config.save(client.runDirectory.toPath());
        }));
    }

    public void delete() {
        if (list.getSelectedOrNull() == null || state != null) return;
        Account acc = list.getSelectedOrNull().account();
        if (hasShiftDown()) {
            Config.accounts.remove(acc);
            Config.save(client.runDirectory.toPath());
            updateButtons();
            list.updateAccounts(search.getText());
            return;
        }
        client.setScreen(new ConfirmScreen(b -> {
            if (b) {
                Config.accounts.remove(acc);
                updateButtons();
                list.updateAccounts(search.getText());
            }
            client.setScreen(this);
        }, Text.translatable("ias.deleteGui.title"), Text.translatable("ias.deleteGui.text", acc.name())));
    }

    private void updateButtons() {
        login.active = list.getSelectedOrNull() != null && state == null;
        loginOffline.active = list.getSelectedOrNull() != null;
        add.active = state == null;
        edit.active = list.getSelectedOrNull() != null && state == null;
        delete.active = list.getSelectedOrNull() != null && state == null;
        reloadSkins.active = list.getSelectedOrNull() != null && state == null && System.currentTimeMillis() > nextSkinUpdate;
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (search.isFocused()) return super.keyPressed(key, scan, mods);
        if (key == GLFW.GLFW_KEY_F5 || key == GLFW.GLFW_KEY_R) {
            reloadSkins();
            return true;
        }
        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
            if (Screen.hasShiftDown()) loginOffline();
            else login();
            return true;
        }
        if (key == GLFW.GLFW_KEY_A || key == GLFW.GLFW_KEY_EQUAL || key == GLFW.GLFW_KEY_KP_ADD) {
            add();
            return true;
        }
        if (key == GLFW.GLFW_KEY_PERIOD || key == GLFW.GLFW_KEY_KP_DIVIDE) {
            edit();
            return true;
        }
        if (key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_MINUS || key == GLFW.GLFW_KEY_KP_SUBTRACT) {
            delete();
            return true;
        }
        return super.keyPressed(key, scan, mods);
    }

    @Override
    public void close() {
        client.setScreen(prev);
    }
}