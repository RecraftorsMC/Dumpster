package mc.recraftors.dumpster.mixin.accessor;

import mc.recraftors.dumpster.utils.accessors.IDoubleBooleanProvider;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FlatChunkGeneratorConfig.class)
public abstract class FlatChunkGeneratorConfigMixin implements IDoubleBooleanProvider {
    @Shadow private boolean hasLakes;

    @Shadow private boolean hasFeatures;

    @Override
    public boolean dumpster$getBool1() {
        return hasLakes;
    }

    @Override
    public boolean dumpster$getBool2() {
        return hasFeatures;
    }
}
