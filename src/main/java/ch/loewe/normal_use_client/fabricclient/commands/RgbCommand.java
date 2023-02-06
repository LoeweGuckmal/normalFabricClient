package ch.loewe.normal_use_client.fabricclient.commands;

import ch.loewe.normal_use_client.fabricclient.modmenu.Config;
import ch.loewe.normal_use_client.fabricclient.modmenu.DefaultConfig.propertyKeys;
import ch.loewe.normal_use_client.fabricclient.openrgb.OpenRGB;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

import static ch.loewe.normal_use_client.fabricclient.modmenu.Config.getStandardColor;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class RgbCommand{

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("maincolor")
                .then(literal("bg")
                        .executes(RgbCommand::bg))
                .then(literal("gelb")
                        .executes(RgbCommand::gelb)));
    }

    private static int bg(CommandContext<FabricClientCommandSource> ctx){
        return mode("bg");
    }
    private static int gelb(CommandContext<FabricClientCommandSource> ctx){
        return mode("gelb");
    }

    private static int mode(String mode){
        if (mode.equals("gelb") || mode.equals("bg")) {
            Config.storeProperty(propertyKeys.standardColor(), mode);
            OpenRGB.loadMode(getStandardColor());
        }
        return 1;
    }
}
