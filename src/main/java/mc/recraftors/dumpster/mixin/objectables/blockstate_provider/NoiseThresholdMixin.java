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
import net.minecraft.world.gen.stateprovider.NoiseThresholdBlockStateProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(NoiseThresholdBlockStateProvider.class)
public abstract class NoiseThresholdMixin extends AbstractNoiseBlockStateProvider implements IObjectable {
    @Shadow @Final private float threshold;

    @Shadow @Final private BlockState defaultState;

    @Shadow @Final private List<BlockState> lowStates;

    @Shadow @Final private List<BlockState> highStates;

    @Shadow @Final private float highChance;

    protected NoiseThresholdMixin(long seed, DoublePerlinNoiseSampler.NoiseParameters noiseParameters, float scale) {
        super(seed, noiseParameters, scale);
    }

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        JsonArray low = new JsonArray();
        lowStates.forEach(state -> low.add(JsonUtils.blockStateJSon(state)));
        JsonArray high = new JsonArray();
        highStates.forEach(state -> high.add(JsonUtils.blockStateJSon(state)));
        o.add("type", new JsonPrimitive(String.valueOf(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(getType()))));
        o.add("seed", new JsonPrimitive(seed));
        o.add("noise", JsonUtils.jsonNoise(noiseParameters));
        o.add("scale", new JsonPrimitive(scale));
        o.add("threshold", new JsonPrimitive(threshold));
        o.add("high_chance", new JsonPrimitive(highChance));
        o.add("default_state", JsonUtils.blockStateJSon(defaultState));
        o.add("low_states", low);
        o.add("high_states", high);
        return o;
    }
}
