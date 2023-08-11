package ch.loewe.normal_use_client.fabricclient.commands;

import ch.loewe.normal_use_client.fabricclient.loewe.WayPoints;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static dev.xpple.clientarguments.arguments.CBlockPosArgumentType.blockPos;
import static dev.xpple.clientarguments.arguments.CBlockPosArgumentType.getCBlockPos;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class WayPointCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("wp")
                .then(literal("add")
                        .then(argument("name", StringArgumentType.string()).executes(ctx -> wp(ctx.getSource(), getString(ctx, "name"))))
                        .then(argument("name", StringArgumentType.string())
                                .then(argument("pos", blockPos()).executes(ctx -> wp(getString(ctx, "name"),  getCBlockPos(ctx, "pos"))))))
                .then(literal("remove")
                        .then(argument("name", StringArgumentType.string()).executes(ctx -> remove(getString(ctx, "name")))))
                .then(literal("toggle").executes((ctx) -> WayPoints.toggle())));
    }

    private static int wp(FabricClientCommandSource src, String name){
        if (name.contains("_") || name.contains(";")) return -1;
        WayPoints.addWayPoint(name, new double[]{src.getPlayer().getX(), src.getPlayer().getY(), src.getPlayer().getZ()});
        return 1;
    }
    private static int wp(String name, BlockPos blockPos){
        if (name.contains("_") || name.contains(";")) return -1;
        WayPoints.addWayPoint(name, new double[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()});
        return 1;
    }
    private static int remove(String name){
        WayPoints.removeWayPoint(name);
        return 1;
    }
}
