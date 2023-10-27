package mc.recraftors.dumpster.mixin.client;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.message.ChatMessageSigner;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Shadow @Final public ClientPlayNetworkHandler networkHandler;

    @Shadow @Final protected MinecraftClient client;

    @Inject(
            method = "sendCommand(Lnet/minecraft/network/message/ChatMessageSigner;Ljava/lang/String;Lnet/minecraft/text/Text;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void onSendCommandBeforeSendInjector(ChatMessageSigner signer, String command, Text preview, CallbackInfo ci) {
        if (MinecraftClient.getInstance().isInSingleplayer()) return;
        int i1 = 0;
        if (command.startsWith("dump-client")) i1 = 2;
        else if (command.startsWith("dump")) i1 = 1;
        if (i1 == 0) return;
        String s1 = i1 == 2 ? "dump-client" : "dump";
        CommandNode<?> node = this.networkHandler.getCommandDispatcher().findNode(List.of(s1));
        try {
            node = (CommandNode<ServerCommandSource>)(node);
            return;
        } catch (ClassCastException e) {
            // literally what we want
        }
        ClientCommandSource source = new ClientCommandSource(this.networkHandler, this.client);
        try {
            this.networkHandler.getCommandDispatcher().execute(command, source);
        } catch (CommandSyntaxException e) {
            this.client.player.sendMessage(Text.empty().append(Texts.toText(e.getRawMessage())).formatted(Formatting.RED));
        }
        ci.cancel();
    }
}
