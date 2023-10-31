package mc.recraftors.dumpster.mixin.client;

import com.mojang.brigadier.CommandDispatcher;
import mc.recraftors.dumpster.client.ClientDumpCommand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientCommandsInjector {
    @Shadow private CommandDispatcher<ClientCommandSource> commandDispatcher;

    @Inject(method = "onCommandTree", at = @At("RETURN"))
    private void onCommandTreeInjector(CommandTreeS2CPacket packet, CallbackInfo ci) {
        if (MinecraftClient.getInstance().isInSingleplayer()) return;
        ClientDumpCommand.register(this.commandDispatcher);
    }
}
