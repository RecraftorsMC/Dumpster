package mc.recraftors.dumpster.mixins.client;

import mc.recraftors.dumpster.utils.ConfigUtils;
import mc.recraftors.dumpster.utils.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class ClientGameStartMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void afterBuildInjector(RunArgs args, CallbackInfo ci) {
        if (ConfigUtils.doAutoDumpRegistriesOnStartup()) {
            Utils.dumpRegistries();
        }
    }
}
