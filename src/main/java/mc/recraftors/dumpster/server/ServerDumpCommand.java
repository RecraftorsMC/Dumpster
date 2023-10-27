package mc.recraftors.dumpster.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import mc.recraftors.dumpster.utils.DumpCall;
import mc.recraftors.dumpster.utils.ConfigUtils;
import mc.recraftors.dumpster.utils.Utils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import static mc.recraftors.dumpster.server.ServerLiteralArgumentBuilder.literal;

public final class ServerDumpCommand {
    static final boolean T = true;
    static final boolean F = false;
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> builder = literal("dump")
                .requires(s -> s.hasPermissionLevel(2))
                .executes(ServerDumpCommand::dumpAll)
                .then(literal("data")
                        .executes(ServerDumpCommand::dumpData)
                        .then(literal("advancements").executes(c -> dumpData(c, new DumpCall.Data(T, F, F, F, F, F, F, F, F, null))))
                        .then(literal("dimensions").executes(c -> dumpData(c, new DumpCall.Data(F, T, F, F, F, F, F, F, F, null))))
                        .then(literal("dimension-types").executes(c -> dumpData(c, new DumpCall.Data(F, F, T, F, F, F, F, F, F, null))))
                        .then(literal("functions").executes(c -> dumpData(c, new DumpCall.Data(F, F, F, T, F, F, F, F, F, null))))
                        .then(literal("loot-tables").executes(c -> dumpData(c, new DumpCall.Data(F, F, F, F, T, F, F, F, F, null))))
                        .then(literal("recipes").executes(c -> dumpData(c, new DumpCall.Data(F, F, F, F, F, T, F, F, F, null))))
                        .then(literal("structures").executes(c -> dumpData(c, new DumpCall.Data(F, F, F, F, F, F, T, F, F, null))))
                        .then(literal("tags").executes(c -> dumpData(c, new DumpCall.Data(F, F, F, F, F, F, F, T, F, null))))
                        .then(literal("worldgen")
                                .executes(c -> dumpData(c, new DumpCall.Data(F, F, F, F, F, F, F, F, T, DumpCall.Worldgen.ALL_TRUE)))
                                .then(literal("biomes").executes(c -> dumpWorldgen(c, T, F, F, F, F, F, F, F)))
                                .then(literal("configured-carvers").executes(c -> dumpWorldgen(c, F, T, F, F, F, F, F, F)))
                                .then(literal("configured-features").executes(c -> dumpWorldgen(c, F, F, T, F, F, F, F, F)))
                                .then(literal("density-functions").executes(c -> dumpWorldgen(c, F, F, F, T, F, F, F, F)))
                                .then(literal("flat-generator-presets").executes(c -> dumpWorldgen(c, F, F, F, F, T, F, F, F)))
                                .then(literal("noise").executes(c -> dumpWorldgen(c, F, F, F, F, F, T, F, F)))
                                .then(literal("noise-settings").executes(c -> dumpWorldgen(c, F, F, F, F, F, F, T, F)))
                                .then(literal("placed-features").executes(c -> dumpWorldgen(c, F, F, F, F, F, F, F, T)))
                        )
                )
                .then(literal("registries")
                        .executes(ServerDumpCommand::dumpReg));
        if (ConfigUtils.isDebugEnabled()) {
            builder.then(literal("debug")
                    .executes(ServerDumpCommand::debug));
        }
        dispatcher.register(builder);
    }

    private static int dumpAll(CommandContext<ServerCommandSource> context) {
        World w = context.getSource().getWorld();
        int n = Utils.dump(w, DumpCall.ALL_TRUE);
        if (n > 0) {
            error(n, context.getSource());
        } else success(context.getSource());
        return 0;
    }

    private static int dumpData(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        int n = Utils.dump(source.getWorld(), new DumpCall(false, true, DumpCall.Data.ALL_TRUE));
        if (n > 0) {
            error(n, source);
        } else success(source);
        return n;
    }

    private static int dumpWorldgen(CommandContext<ServerCommandSource> context, boolean b1, boolean b2,
                                    boolean b3, boolean b4, boolean b5, boolean b6, boolean b7, boolean b8) {
        return dumpData(context, new DumpCall.Data(F,F,F,F,F,F,F,F,T, new DumpCall.Worldgen(b1, b2, b3, b4, b5, b6, b7, b8,F,F,F,F)));
    }

    private static int dumpData(CommandContext<ServerCommandSource> context, DumpCall.Data call) {
        ServerCommandSource source = context.getSource();
        int n = Utils.dump(source.getWorld(), new DumpCall(false, true, call));
        if (n > 0) {
            error(n, source);
        } else success(source);
        return n;
    }

    private static int dumpReg(CommandContext<ServerCommandSource> context) {
        int n = Utils.dump(null, new DumpCall(true, false, null));
        if (n > 0) {
            error(n, context.getSource());
        } else success(context.getSource());
        return n;
    }

    private static int debug(CommandContext<ServerCommandSource> context) {
        Utils.debug();
        return 0;
    }

    private static void error(int n, ServerCommandSource source) {
        source.sendFeedback(Text.literal(String.format("%d errors occurred trying to dump server data", n)), true);
    }

    private static void success(ServerCommandSource source) {
        source.sendFeedback(Text.literal("Dump successful"), true);
    }

    private ServerDumpCommand() {}
}
