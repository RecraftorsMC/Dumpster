package mc.recraftors.dumpster.mixins.server;

import com.mojang.brigadier.CommandDispatcher;
import mc.recraftors.dumpster.server.ServerDumpCommand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.SERVER)
@Mixin(CommandManager.class)
public abstract class ServerCommandManagerMixin {
    @Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initTailInjector(CommandManager.RegistrationEnvironment e, CommandRegistryAccess c, CallbackInfo ci) {
        ServerDumpCommand.register(dispatcher);
    }
}
