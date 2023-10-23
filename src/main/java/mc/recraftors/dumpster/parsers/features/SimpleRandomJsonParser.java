package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.SimpleRandomFeatureConfig;

@TargetFeatureConfigType(value = "simple_random", supports = "simple_random_selector")
public class SimpleRandomJsonParser implements FeatureJsonParser {
    private SimpleRandomFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof SimpleRandomFeatureConfig s) {
            this.config = s;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        JsonArray features = new JsonArray();
        config.features.forEach(f -> features.add(JsonUtils.jsonPlacedFeatureRegEntry(f)));
        main.add("features", features);
        return main;
    }
}
