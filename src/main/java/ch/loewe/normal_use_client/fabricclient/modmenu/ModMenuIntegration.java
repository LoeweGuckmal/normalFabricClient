package ch.loewe.normal_use_client.fabricclient.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

public class ModMenuIntegration implements ModMenuApi {
    public ModMenuIntegration(){}
    @Override
    public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
        return ConfigScreen::new;
    }
}
