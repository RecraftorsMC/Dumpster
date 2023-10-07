package mc.recraftors.dumpster.mixins.client;

import mc.recraftors.dumpster.utils.ConfigUtils;
import mc.recraftors.dumpster.utils.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.LocalDateTime;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Shadow public abstract ClientWorld getWorld();

    @Inject(method = "onSynchronizeRecipes", at = @At("TAIL"))
    private void onSynchronizedRecipesTailInjector(SynchronizeRecipesS2CPacket packet, CallbackInfo ci) {
        if (ConfigUtils.doAutoDumpResourcesOnReload()) {
            Utils.dumpData(this.getWorld(), LocalDateTime.now());
        }
        if (ConfigUtils.doAutoDumpRegistriesOnReload()) {
            Utils.dumpRegistries(LocalDateTime.now());
        }
    }
}
