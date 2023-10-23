package mc.recraftors.dumpster.mixin.accessor;

import mc.recraftors.dumpster.utils.accessors.IFloatProvider;
import net.minecraft.world.biome.BiomeParticleConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BiomeParticleConfig.class)
public abstract class BiomeParticleConfigMixin implements IFloatProvider {
    @Shadow @Final private float probability;

    @Override
    public float dumpster$getFloat() {
        return probability;
    }
}
