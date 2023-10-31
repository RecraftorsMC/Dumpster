package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;

@TargetFeatureConfigType(value = "single_state", supports = {"iceberg", "forest_rock"})
public class SingleStateJsonParser implements FeatureJsonParser {
    private SingleStateFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof SingleStateFeatureConfig s) {
            this.config = s;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("state", JsonUtils.blockStateJSon(config.state));
        return main;
    }
}
