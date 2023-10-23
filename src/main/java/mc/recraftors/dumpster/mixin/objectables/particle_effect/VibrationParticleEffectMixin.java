package mc.recraftors.dumpster.mixin.objectables.particle_effect;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.Objectable;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.PositionSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VibrationParticleEffect.class)
public abstract class VibrationParticleEffectMixin implements IObjectable {
    @Shadow public abstract ParticleType<VibrationParticleEffect> getType();

    @Shadow @Final private PositionSource destination;

    @Shadow @Final private int arrivalInTicks;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.PARTICLE_TYPE.getId(this.getType()))));
        JsonObject d = ((Objectable)destination).toJson();
        o.add("destination", d);
        o.add("arrival_in_ticks", new JsonPrimitive(arrivalInTicks));
        return o;
    }
}
