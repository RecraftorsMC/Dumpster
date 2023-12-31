package mc.recraftors.dumpster.mixin.client;

import mc.recraftors.dumpster.utils.DumpCall;
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

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Shadow public abstract ClientWorld getWorld();

    @Inject(method = "onSynchronizeRecipes", at = @At("TAIL"))
    private void onSynchronizedRecipesTailInjector(SynchronizeRecipesS2CPacket packet, CallbackInfo ci) {
        boolean b1 = ConfigUtils.doAutoDumpResourcesOnReload();
        boolean b2 = ConfigUtils.doAutoDumpRegistriesOnReload();
        if (b1 || b2) Utils.dump(getWorld(), new DumpCall(b1, b2, DumpCall.Data.ALL_TRUE));
    }
}
