package mc.recraftors.dumpster.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import mc.recraftors.dumpster.utils.ConfigUtils;
import mc.recraftors.dumpster.utils.Utils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.time.LocalDateTime;

import static mc.recraftors.dumpster.server.ServerLiteralArgumentBuilder.literal;

public final class ServerDumpCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final boolean T = true;
        final boolean F = false;
        LiteralArgumentBuilder<ServerCommandSource> builder = literal("dump")
                .requires(s -> s.hasPermissionLevel(2))
                .executes(ServerDumpCommand::dumpAll)
                .then(literal("data")
                        .executes(ServerDumpCommand::dumpData)
                        .then(literal("advancements").executes(c -> dumpData(c, F, F, F, T, F, F, F)))
                        .then(literal("dimension-types").executes(c -> dumpData(c, F, F, F, F, T, F, F)))
                        .then(literal("functions").executes(c -> dumpData(c, F, F, F, F, F, T, F)))
                        .then(literal("loot-tables").executes(c -> dumpData(c, F, F, T, F, F, F, F)))
                        .then(literal("recipes").executes(c -> dumpData(c, F, T, F, F, F, F, F)))
                        .then(literal("structures").executes(c -> dumpData(c, F, F, F, F, F, F, T)))
                        .then(literal("tags").executes(c -> dumpData(c, T, F, F, F, F, F, F)))
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
        LocalDateTime now = LocalDateTime.now();
        int n = Utils.dumpRegistries(now) + Utils.dumpData(w, now);
        if (n > 0) {
            error(n, context.getSource());
        } else success(context.getSource());
        return 0;
    }

    private static int dumpData(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        int n = Utils.dumpData(source.getWorld(), LocalDateTime.now());
        if (n > 0) {
            error(n, source);
        } else success(source);
        return n;
    }

    private static int dumpData(CommandContext<ServerCommandSource> context, boolean b1, boolean b2, boolean b3,
                                boolean b4, boolean b5, boolean b6, boolean b7) {
        ServerCommandSource source = context.getSource();
        int n = Utils.dumpData(source.getWorld(), LocalDateTime.now(), b1, b2, b3, b4, b5, b6, b7);
        if (n > 0) {
            error(n, source);
        } else success(source);
        return n;
    }

    private static int dumpReg(CommandContext<ServerCommandSource> context) {
        int n = Utils.dumpRegistries(LocalDateTime.now());
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
