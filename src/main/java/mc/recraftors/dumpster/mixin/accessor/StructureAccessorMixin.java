package mc.recraftors.dumpster.mixin.accessor;

import mc.recraftors.dumpster.utils.accessors.IObjectProvider;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.StructureAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StructureAccessor.class)
public class StructureAccessorMixin implements IObjectProvider<GeneratorOptions> {
    @Shadow @Final private GeneratorOptions options;

    @Override
    public GeneratorOptions dumpster$getObject() {
        return this.options;
    }
}
