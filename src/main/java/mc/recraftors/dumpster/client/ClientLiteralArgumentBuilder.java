package mc.recraftors.dumpster.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.network.ClientCommandSource;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class ClientLiteralArgumentBuilder extends LiteralArgumentBuilder<ClientCommandSource> {
    public ClientLiteralArgumentBuilder(String literal) {
        super(literal);
    }

    @Contract("_ -> new")
    public static @NotNull LiteralArgumentBuilder<ClientCommandSource> literal(String name) {
        return new ClientLiteralArgumentBuilder(name);
    }
}
