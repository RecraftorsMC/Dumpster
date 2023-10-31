package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureConfig;

@TargetFeatureConfigType(value = "random", supports = "random_selector")
public class RandomJsonParser implements FeatureJsonParser {
    private RandomFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof RandomFeatureConfig r) {
            this.config = r;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        JsonArray features = new JsonArray();
        config.features.forEach(entry -> {
            JsonObject o = new JsonObject();
            o.add("feature", JsonUtils.jsonPlacedFeatureRegEntry(entry.feature));
            o.add("chance", new JsonPrimitive(entry.chance));
            features.add(o);
        });
        main.add("features", features);
        main.add("default", JsonUtils.jsonPlacedFeatureRegEntry(config.defaultFeature));
        return main;
    }
}
