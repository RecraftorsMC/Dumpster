package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;

@TargetFeatureConfigType(value = "random_patch", supports = {"flower", "no_bonemeal_flower"})
public class RandomPatchJsonParser implements FeatureJsonParser {
    private RandomPatchFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof RandomPatchFeatureConfig r) {
            this.config = r;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("tries", new JsonPrimitive(config.tries()));
        main.add("xz_spread", new JsonPrimitive(config.xzSpread()));
        main.add("y_spread", new JsonPrimitive(config.ySpread()));
        main.add("feature", JsonUtils.jsonPlacedFeatureRegEntry(config.feature()));
        return main;
    }
}
