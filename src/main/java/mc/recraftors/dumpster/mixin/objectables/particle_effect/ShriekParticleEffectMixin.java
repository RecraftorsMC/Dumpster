package mc.recraftors.dumpster.mixin.objectables.particle_effect;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ShriekParticleEffect.class)
public abstract class ShriekParticleEffectMixin implements IObjectable {
    @Shadow public abstract ParticleType<ShriekParticleEffect> getType();

    @Shadow @Final private int delay;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(Registry.PARTICLE_TYPE.getId(getType()).toString()));
        o.add("delay", new JsonPrimitive(delay));
        return o;
    }
}
