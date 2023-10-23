package mc.recraftors.dumpster.mixin.objectables.blockstate_provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.stateprovider.AbstractNoiseBlockStateProvider;
import net.minecraft.world.gen.stateprovider.NoiseBlockStateProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(NoiseBlockStateProvider.class)
public abstract class NoiseMixin extends AbstractNoiseBlockStateProvider implements IObjectable {
    @Shadow @Final protected List<BlockState> states;

    protected NoiseMixin(long seed, DoublePerlinNoiseSampler.NoiseParameters noiseParameters, float scale) {
        super(seed, noiseParameters, scale);
    }

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        JsonArray s = new JsonArray();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(getType()))));
        o.add("seed", new JsonPrimitive(seed));
        o.add("noise", JsonUtils.noiseJson(noiseParameters));
        o.add("scale", new JsonPrimitive(scale));
        states.forEach(state -> s.add(JsonUtils.blockStateJSon(state)));
        o.add("states", s);
        return o;
    }
}
