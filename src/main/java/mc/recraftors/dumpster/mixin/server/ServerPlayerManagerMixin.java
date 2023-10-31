package mc.recraftors.dumpster.mixin.server;

import mc.recraftors.dumpster.utils.DumpCall;
import mc.recraftors.dumpster.utils.ConfigUtils;
import mc.recraftors.dumpster.utils.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Environment(EnvType.SERVER)
@Mixin(PlayerManager.class)
public abstract class ServerPlayerManagerMixin {
    @Shadow public abstract MinecraftServer getServer();

    @Inject(method = "onDataPacksReloaded", at = @At("TAIL"))
    private void datapackReloadTailInjector(CallbackInfo ci) {
        try {
            ConfigUtils.reload();
        } catch (IOException e) {
            Utils.LOGGER.error("An exception occurred while trying to reload the config", e);
        }
        boolean b1 = ConfigUtils.doAutoDumpResourcesOnReload();
        boolean b2 = ConfigUtils.doAutoDumpRegistriesOnReload();
        if (b1 || b2) Utils.dump(getServer().getOverworld(), new DumpCall(b1, b2, DumpCall.Data.ALL_TRUE));
    }
}
