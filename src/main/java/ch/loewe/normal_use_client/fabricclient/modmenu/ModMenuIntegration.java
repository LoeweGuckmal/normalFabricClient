package ch.loewe.normal_use_client.fabricclient.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.config.ModMenuConfig;
import net.minecraft.client.gui.screen.Screen;

public class ModMenuIntegration implements ModMenuApi {
    public ModMenuIntegration(){}
    @Override
    public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
        return ConfigScreen::new;
    }

    public static int buttonOffset() {
        try {
            ModMenuConfig.ModsButtonStyle style = (ModMenuConfig.ModsButtonStyle)ModMenuConfig.MODS_BUTTON_STYLE.getValue();
            if (style == ModMenuConfig.ModsButtonStyle.ICON) {
                return -48;
            }
        } catch (Throwable ignored) {
        }

        return -24;
    }
}
