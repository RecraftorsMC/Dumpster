package mc.recraftors.dumpster.mixin.objectables.particle_effect;

import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ParticleEffect.class)
public interface ParticleEffectMixin extends IObjectable {
}
