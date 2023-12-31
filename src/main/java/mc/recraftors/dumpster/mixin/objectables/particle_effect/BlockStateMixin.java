package mc.recraftors.dumpster.mixin.objectables.particle_effect;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.block.BlockState;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockStateParticleEffect.class)
public abstract class BlockStateMixin implements IObjectable {
    @Shadow @Final private BlockState blockState;

    @Shadow public abstract ParticleType<BlockStateParticleEffect> getType();

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.PARTICLE_TYPE.getId(this.getType()))));
        JsonObject v = JsonUtils.blockStateJSon(blockState);
        o.add("value", v);
        return o;
    }
}
