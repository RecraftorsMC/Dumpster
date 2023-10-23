package mc.recraftors.dumpster.mixin.objectables.particle_effect;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DustColorTransitionParticleEffect.class)
public abstract class DustColorTransitionParticleEffectMixin extends AbstractDustParticleEffect implements IObjectable {
    @Shadow public abstract ParticleType<DustColorTransitionParticleEffect> getType();

    @Shadow @Final private Vec3f toColor;

    DustColorTransitionParticleEffectMixin(Vec3f color, float scale) {
        super(color, scale);
    }

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.PARTICLE_TYPE.getId(this.getType()))));
        JsonArray f = new JsonArray();
        JsonArray t = new JsonArray();
        f.add(color.getX());
        f.add(color.getY());
        f.add(color.getZ());
        o.add("fromColor", f);
        t.add(toColor.getX());
        t.add(toColor.getY());
        t.add(toColor.getZ());
        o.add("toColor", t);
        o.add("scale", new JsonPrimitive(scale));
        return o;
    }
}
