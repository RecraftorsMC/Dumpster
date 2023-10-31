package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;

@TargetFeatureConfigType("simple_block")
public class SimpleBlockJsonParser implements FeatureJsonParser {
    private SimpleBlockFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof SimpleBlockFeatureConfig s) {
            this.config = s;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("to_place", JsonUtils.objectJson(config.toPlace()));
        return main;
    }
}
