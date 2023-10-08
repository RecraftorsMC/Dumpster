package mc.recraftors.dumpster.server;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.SERVER)
public final class ServerLiteralArgumentBuilder extends LiteralArgumentBuilder<ServerCommandSource> {
    public ServerLiteralArgumentBuilder(String literal) {
        super(literal);
    }

    public static @NotNull LiteralArgumentBuilder<ServerCommandSource> literal(String name) {
        return new ServerLiteralArgumentBuilder(name);
    }
}
