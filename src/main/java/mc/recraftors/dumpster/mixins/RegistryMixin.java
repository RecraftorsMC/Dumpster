package mc.recraftors.dumpster.mixins;

import com.mojang.serialization.Lifecycle;
import mc.recraftors.dumpster.utils.Utils;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Registry.class)
public abstract class RegistryMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initTailInjector(RegistryKey<?> key, Lifecycle lifecycle, CallbackInfo ci) {
        Utils.reg((Registry<?>) ((Object) this));
    }
}
