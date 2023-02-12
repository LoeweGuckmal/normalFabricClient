package ch.loewe.normal_use_client.fabricclient.account.ias;

import ch.loewe.normal_use_client.fabricclient.account.Config;
import ch.loewe.normal_use_client.fabricclient.account.Expression;
import ch.loewe.normal_use_client.fabricclient.account.ias.gui.AccountListScreen;
import ch.loewe.normal_use_client.fabricclient.modmenu.ModMenuIntegration;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IAS {
    public static final Identifier IAS_BUTTON = new Identifier("ias", "textures/gui/iasbutton.png");
    public static final Map<UUID, Identifier> SKIN_CACHE = new HashMap();
    public static boolean modMenu;

    public IAS() {
    }

    public static void onInitializeClient() {
        modMenu = FabricLoader.getInstance().isModLoaded("modmenu");
        ClientLifecycleEvents.CLIENT_STARTED.register((mc) -> {
            Config.load(mc.runDirectory.toPath());
        });
        ScreenEvents.AFTER_INIT.register((mc, screen, w, h) -> {
            TexturedButtonWidget temp;
            int txx;
            int tx;
            if (screen instanceof MultiplayerScreen && Config.multiplayerScreenButton) {
                txx = w / 2 + 4 + 76 + 79;
                tx = h - 28;

                try {
                    txx = (int) Expression.parseWidthHeight(Config.titleScreenButtonX, screen.width, screen.height);
                    tx = (int)Expression.parseWidthHeight(Config.titleScreenButtonY, screen.width, screen.height);
                } catch (Throwable var8) {
                    txx = w / 2 + 4 + 76 + 79;
                    tx = h - 28;
                }

                temp = new TexturedButtonWidget(txx, tx, 20, 20, 0, 0, 20, IAS_BUTTON, 256, 256, (btn) -> {
                    mc.setScreen(new AccountListScreen(screen));
                }, Text.literal("In-Game Account Switcher"));
                temp.setTooltip(Tooltip.of(Text.literal("In-Game Account Switcher")));
                Screens.getButtons(screen).add(temp);
            }

            if (screen instanceof TitleScreen) {
                if (Config.titleScreenButton) {
                    txx = w / 2 + 104;
                    tx = h / 4 + 48 + 72 + (modMenu ? ModMenuIntegration.buttonOffset() : -24);

                    try {
                        txx = (int)Expression.parseWidthHeight(Config.titleScreenButtonX, screen.width, screen.height);
                        tx = (int)Expression.parseWidthHeight(Config.titleScreenButtonY, screen.width, screen.height);
                    } catch (Throwable var10) {
                        txx = w / 2 + 104;
                        tx = h / 4 + 48 + 72 + (modMenu ? ModMenuIntegration.buttonOffset() : -24);
                    }

                    temp = new TexturedButtonWidget(txx, tx, 20, 20, 0, 0, 20, IAS_BUTTON, 256, 256, (btn) -> {
                        mc.setScreen(new AccountListScreen(screen));
                    }, Text.literal("In-Game Account Switcher"));
                    temp.setTooltip(Tooltip.of(Text.literal("In-Game Account Switcher")));
                    Screens.getButtons(screen).add(temp);
                }

                if (Config.titleScreenText) {
                    try {
                        txx = (int)Expression.parseWidthHeight(Config.titleScreenTextX, screen.width, screen.height);
                        tx = (int)Expression.parseWidthHeight(Config.titleScreenTextY, screen.width, screen.height);
                        int finalTxx = txx;
                        int finalTx = tx;
                        ScreenEvents.afterRender(screen).register((s, ms, mx, my, delta) -> {
                            if (Config.titleScreenTextAlignment == Config.Alignment.LEFT) {
                                InGameHud.drawTextWithShadow(ms, mc.textRenderer, Text.translatable("ias.title", new Object[]{mc.getSession().getUsername()}), finalTxx, finalTx, -3372920);
                            } else if (Config.titleScreenTextAlignment == Config.Alignment.RIGHT) {
                                InGameHud.drawTextWithShadow(ms, mc.textRenderer, Text.translatable("ias.title", new Object[]{mc.getSession().getUsername()}), finalTxx - mc.textRenderer.getWidth(Text.translatable("ias.title", new Object[]{mc.getSession().getUsername()})), finalTx, -3372920);
                            } else {
                                InGameHud.drawCenteredText(ms, mc.textRenderer, Text.translatable("ias.title", new Object[]{mc.getSession().getUsername()}), finalTxx, finalTx, -3372920);
                            }
                        });
                    } catch (Throwable var9) {
                        tx = w / 2;
                        int ty = h / 4 + 48 + 72 + 12 + (modMenu ? 32 : 22);
                        int finalTx1 = tx;
                        ScreenEvents.afterRender(screen).register((s, ms, mx, my, delta) -> {
                            InGameHud.drawCenteredText(ms, mc.textRenderer, Text.translatable("ias.title", new Object[]{mc.getSession().getUsername()}), finalTx1, ty, -3372920);
                        });
                    }
                }
            }

        });
    }
}
