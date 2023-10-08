package mc.recraftors.dumpster.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import mc.recraftors.dumpster.utils.Utils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.time.LocalDateTime;

import static mc.recraftors.dumpster.server.ServerLiteralArgumentBuilder.literal;

public final class ServerDumpCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("dump")
                .requires(s -> s.hasPermissionLevel(2))
                .executes(ServerDumpCommand::dumpAll)
                .then(literal("data")
                        .executes(ServerDumpCommand::dumpData))
                .then(literal("registries")
                        .executes(ServerDumpCommand::dumpReg)));
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

    private static int dumpReg(CommandContext<ServerCommandSource> context) {
        int n = Utils.dumpRegistries(LocalDateTime.now());
        if (n > 0) {
            error(n, context.getSource());
        } else success(context.getSource());
        return n;
    }

    private static void error(int n, ServerCommandSource source) {
        source.sendFeedback(Text.literal(String.format("%d errors occurred trying to dump server data", n)), true);
    }

    private static void success(ServerCommandSource source) {
        source.sendFeedback(Text.literal("Dump successful"), true);
    }

    private ServerDumpCommand() {}
}
