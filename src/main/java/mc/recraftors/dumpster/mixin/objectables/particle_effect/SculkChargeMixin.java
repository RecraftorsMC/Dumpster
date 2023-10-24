package mc.recraftors.dumpster.mixin.objectables.particle_effect;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SculkChargeParticleEffect;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SculkChargeParticleEffect.class)
public abstract class SculkChargeMixin implements IObjectable {
    @Shadow public abstract ParticleType<SculkChargeParticleEffect> getType();

    @Shadow @Final private float roll;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.PARTICLE_TYPE.getId(this.getType()))));
        o.add("roll", new JsonPrimitive(roll));
        return o;
    }
}
