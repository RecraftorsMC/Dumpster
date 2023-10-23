package mc.recraftors.dumpster.mixin.objectables.blockstate_provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.block.BlockState;
import net.minecraft.util.dynamic.Range;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.stateprovider.DualNoiseBlockStateProvider;
import net.minecraft.world.gen.stateprovider.NoiseBlockStateProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Objects;

@Mixin(DualNoiseBlockStateProvider.class)
public abstract class DualNoiseMixin extends NoiseBlockStateProvider implements IObjectable {
    @Override
    @Shadow protected abstract BlockStateProviderType<?> getType();

    @Shadow @Final private DoublePerlinNoiseSampler.NoiseParameters slowNoiseParameters;

    @Shadow @Final private float slowScale;

    @Shadow @Final private Range<Integer> variety;

    DualNoiseMixin(long seed, DoublePerlinNoiseSampler.NoiseParameters noiseParameters, float scale, List<BlockState> states) {
        super(seed, noiseParameters, scale, states);
    }

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(getType()).toString()));
        o.add("seed", new JsonPrimitive(seed));
        o.add("noise", JsonUtils.noiseJson(noiseParameters));
        o.add("scale", new JsonPrimitive(scale));
        o.add("slow_noise", JsonUtils.noiseJson(slowNoiseParameters));
        o.add("slow_scale", new JsonPrimitive(slowScale));
        if (Objects.equals(variety.maxInclusive(), variety.minInclusive())) {
            o.add("variety", new JsonPrimitive(variety.maxInclusive()));
        } else {
            JsonObject v = new JsonObject();
            v.add("min_inclusive", new JsonPrimitive(variety.minInclusive()));
            v.add("max_inclusive", new JsonPrimitive(variety.maxInclusive()));
            o.add("variety", v);
        }
        JsonArray s = new JsonArray();
        states.forEach(state -> s.add(JsonUtils.blockStateJSon(state)));
        o.add("states", s);
        return o;
    }
}
