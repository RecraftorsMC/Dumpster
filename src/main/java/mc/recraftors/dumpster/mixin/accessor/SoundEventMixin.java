package mc.recraftors.dumpster.mixin.accessor;

import mc.recraftors.dumpster.utils.accessors.IBooleanProvider;
import mc.recraftors.dumpster.utils.accessors.IFloatProvider;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SoundEvent.class)
public class SoundEventMixin implements IBooleanProvider, IFloatProvider {
    @Shadow @Final private boolean staticDistance;

    @Shadow @Final private float distanceToTravel;

    @Override
    public boolean dumpster$getBool() {
        return staticDistance;
    }

    @Override
    public float dumpster$getFloat() {
        return distanceToTravel;
    }
}
