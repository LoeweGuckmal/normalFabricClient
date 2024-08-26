package ch.loewe.normal_use_client.fabricclient.account.ias;
//
import ch.loewe.normal_use_client.fabricclient.account.Config;
import ch.loewe.normal_use_client.fabricclient.account.Expression;
import ch.loewe.normal_use_client.fabricclient.account.ias.gui.AccountListScreen;
import ch.loewe.normal_use_client.fabricclient.modmenu.ModMenuIntegration;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IAS {
    public static final ButtonTextures IAS_BUTTON = new ButtonTextures(Identifier.of("ias", "iasbutton_plain"), Identifier.of("ias", "iasbutton_hover"));
    public static final Map<UUID, SkinTextures> SKIN_CACHE = new HashMap<>();
    public static boolean modMenu;

    public static void onInitializeClient() {
        modMenu = FabricLoader.getInstance().isModLoaded("modmenu");
        ClientLifecycleEvents.CLIENT_STARTED.register((mc) -> {
            Config.load(mc.runDirectory.toPath());
        });
        ScreenEvents.AFTER_INIT.register((mc, screen, w, h) -> {
            ButtonWidget temp;
            if (screen instanceof MultiplayerScreen && Config.multiplayerScreenButton) {
                int bx;
                int by;
                try {
                    bx = (int) Expression.parseWidthHeight(Config.titleScreenButtonX, screen.width, screen.height);
                    by = (int) Expression.parseWidthHeight(Config.titleScreenButtonY, screen.width, screen.height);
                } catch (Throwable t) {
                    bx = w / 2 + 4 + 76 + 79;
                    by = h - 28;
                }
                temp = new TexturedButtonWidget(bx, by, 20, 20, IAS_BUTTON,
                        btn -> mc.setScreen(new AccountListScreen(screen)),
                        Text.literal("In-Game Account Switcher"));
                temp.setTooltip(Tooltip.of(Text.literal("In-Game Account Switcher")));
                Screens.getButtons(screen).add(temp);
            }
            if (screen instanceof TitleScreen) {
                if (Config.titleScreenButton) {
                    int bx;
                    int by;
                    try {
                        bx = (int) Expression.parseWidthHeight(Config.titleScreenButtonX, screen.width, screen.height);
                        by = (int) Expression.parseWidthHeight(Config.titleScreenButtonY, screen.width, screen.height);
                    } catch (Throwable t) {
                        bx = w / 2 + 104;
                        by = h / 4 + 48 + 72 + (IAS.modMenu ? ModMenuIntegration.buttonOffset() : -24);
                    }
                    temp = new TexturedButtonWidget(bx, by, 20, 20, IAS_BUTTON,
                            btn -> mc.setScreen(new AccountListScreen(screen)),
                            Text.literal("In-Game Account Switcher"));
                    temp.setTooltip(Tooltip.of(Text.literal("In-Game Account Switcher")));
                    Screens.getButtons(screen).add(temp);
                }
                if (Config.titleScreenText) {
                    try {
                        int tx = (int) Expression.parseWidthHeight(Config.titleScreenTextX, screen.width, screen.height);
                        int ty = (int) Expression.parseWidthHeight(Config.titleScreenTextY, screen.width, screen.height);
                        ScreenEvents.afterRender(screen).register((s, ms, mx, my, delta) -> {
                            if (Config.titleScreenTextAlignment == Config.Alignment.LEFT) {
                                ms.drawTextWithShadow(mc.textRenderer, Text.translatable("ias.title", mc.getSession().getUsername()), tx, ty, 0xFFCC8888);
                                return;
                            }
                            if (Config.titleScreenTextAlignment == Config.Alignment.RIGHT) {
                                ms.drawTextWithShadow(mc.textRenderer, Text.translatable("ias.title", mc.getSession().getUsername()), tx - mc.textRenderer.getWidth(Text.translatable("ias.title", mc.getSession().getUsername())), ty, 0xFFCC8888);
                                return;
                            }
                            ms.drawCenteredTextWithShadow(mc.textRenderer, Text.translatable("ias.title", mc.getSession().getUsername()), tx, ty, 0xFFCC8888);
                        });
                    } catch (Throwable t) {
                        int tx = w / 2;
                        int ty = h / 4 + 48 + 72 + 12 + (IAS.modMenu ? 32 : 22);
                        ScreenEvents.afterRender(screen).register((s, ms, mx, my, delta) -> {
                            ms.drawCenteredTextWithShadow(mc.textRenderer, Text.translatable("ias.title", mc.getSession().getUsername()), tx, ty, 0xFFCC8888);
                        });
                    }
                }
            }
        });
    }
}
