package mc.recraftors.dumpster.mixin.objectables.particle_effect;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DefaultParticleType.class)
public abstract class DefaultMixin implements IObjectable {
    @Shadow public abstract DefaultParticleType getType();

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.PARTICLE_TYPE.getId(this.getType()))));
        return o;
    }
}
