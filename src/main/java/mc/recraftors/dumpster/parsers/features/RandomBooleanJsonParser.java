package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RandomBooleanFeatureConfig;

@TargetFeatureConfigType(value = "random_boolean", supports = "random_boolean_selector")
public class RandomBooleanJsonParser implements FeatureJsonParser {
    private RandomBooleanFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof RandomBooleanFeatureConfig r) {
            this.config = r;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("feature_false", JsonUtils.jsonPlacedFeatureRegEntry(config.featureFalse));
        main.add("feature_true", JsonUtils.jsonPlacedFeatureRegEntry(config.featureTrue));
        return main;
    }
}
