package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

@TargetFeatureConfigType(value = "count", supports = "sea_pickle")
public class CountJsonParser implements FeatureJsonParser {
    private CountConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof CountConfig c) {
            this.config = c;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("count", JsonUtils.objectJson(config.getCount()));
        return main;
    }
}
