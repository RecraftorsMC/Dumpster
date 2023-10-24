package mc.recraftors.dumpster.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import mc.recraftors.dumpster.utils.ConfigUtils;
import mc.recraftors.dumpster.utils.DumpCall;
import mc.recraftors.dumpster.utils.FileUtils;
import mc.recraftors.dumpster.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.List;

import static mc.recraftors.dumpster.client.ClientLiteralArgumentBuilder.literal;

public final class ClientDumpCommand {
    public static void register(CommandDispatcher<ClientCommandSource> dispatcher) {
        CommandNode<?> dump = dispatcher.findNode(List.of("dump"));
        LiteralArgumentBuilder<ClientCommandSource> builder = literal(dump == null ? "dump" : "dump-client")
                .executes(ClientDumpCommand::dumpAll)
                .then(literal("data").executes(ClientDumpCommand::dumpData))
                .then(literal("registries").executes(ClientDumpCommand::dumpReg));
        if (ConfigUtils.isDebugEnabled()) {
            builder.then(literal("debug").executes(ClientDumpCommand::debug));
        }
        dispatcher.register(builder);
    }

    private static int dumpAll(CommandContext<ClientCommandSource> context) {
        FileUtils.clearIfNeeded();
        World world = MinecraftClient.getInstance().world;
        int n = Utils.dump(world, DumpCall.ALL_TRUE);
        if (n > 0) {
            toastError(n);
        }
        return n;
    }

    private static int dumpData(CommandContext<ClientCommandSource> context) {
        FileUtils.clearIfNeeded();
        RegistryKey<World> key = context.getSource().getWorldKeys().iterator().next();
        World world = (World) Registry.REGISTRIES.get(key.getValue()).iterator().next();
        int n = Utils.dump(world, new DumpCall(false, true, DumpCall.Data.ALL_TRUE));
        if (n > 0) {
            toastError(n);
        }
        return n;
    }

    private static int dumpReg(CommandContext<ClientCommandSource> context) {
        FileUtils.clearIfNeeded();
        int n = Utils.dump(null, new DumpCall(true, false, null));
        if (n > 0) {
            toastError(n);
        }
        return n;
    }

    private static int debug(CommandContext<ClientCommandSource> context) {
        Utils.debug();
        return 0;
    }

    private static void toastError(int i) {
        MinecraftClient.getInstance().getToastManager().add(
                new SystemToast(
                        SystemToast.Type.PACK_LOAD_FAILURE,
                        Text.translatable(Utils.ERROR_TOAST_TITLE),
                        Text.translatable(Utils.ERROR_TOAST_DESC, i)
                ));
    }

    private ClientDumpCommand() {}
}
