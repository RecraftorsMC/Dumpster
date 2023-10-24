package mc.recraftors.dumpster.mixin.objectables.particle_effect;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DustParticleEffect.class)
public abstract class DustMixin extends AbstractDustParticleEffect implements IObjectable {

    @Shadow public abstract ParticleType<DustParticleEffect> getType();

    DustMixin(Vec3f color, float scale) {
        super(color, scale);
    }

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.PARTICLE_TYPE.getId(this.getType()))));
        JsonArray c = new JsonArray();
        c.add(color.getX());
        c.add(color.getY());
        c.add(color.getZ());
        o.add("color", c);
        o.add("scale", new JsonPrimitive(scale));
        return o;
    }
}
