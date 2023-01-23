package ch.loewe.normal_use_client.fabricclient.commands;

import ch.loewe.normal_use_client.fabricclient.client.FabricClientClient;
import ch.loewe.normal_use_client.fabricclient.openrgb.OpenRGB;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class RgbCommand{
    public static boolean toggled = false;

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("maincolor")
                .then(literal("bg")
                        .executes(RgbCommand::bg))
                .then(literal("gelb")
                        .executes(RgbCommand::gelb))
                .then(literal("toggle")
                        .executes(RgbCommand::toggle)));
    }

    private static int bg(CommandContext<FabricClientCommandSource> ctx){
        return mode("bg");
    }
    private static int gelb(CommandContext<FabricClientCommandSource> ctx){
        return mode("gelb");
    }
    private static int toggle(CommandContext<FabricClientCommandSource> ctx){
        toggled = !toggled;
        if (toggled) {
            ctx.getSource().getPlayer().sendMessage(Text.literal("The Plugin won't change the light anymore!"));
            return 1;
        }
        ctx.getSource().getPlayer().sendMessage(Text.literal("The Plugin will change the light again."));
        return 1;
    }

    private static int mode(String mode){
        if (mode.equals("gelb") || mode.equals("bg")) {
            FabricClientClient.mode = mode;
            if (!toggled)
                OpenRGB.loadMode(mode);
        }
        return 1;
    }
}
