package mc.recraftors.dumpster.mixin.server;

import mc.recraftors.dumpster.utils.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.dedicated.DedicatedServerWatchdog;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.SERVER)
@Mixin(DedicatedServerWatchdog.class)
public class ServerWatchdogMixin {

    @Shadow @Final private MinecraftDedicatedServer server;

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/MinecraftDedicatedServer;getTimeReference()J"))
    private long toggleableServerTickTimeInjector(MinecraftDedicatedServer instance) {
        boolean b = true;
        if (Utils.lock.tryLock()) {
            b = false;
        } else {
            Utils.lock.lock();
        }
        Utils.lock.unlock();
        return b ? Util.getMeasuringTimeMs() : server.getTimeReference();
    }
}
