package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.TwistingVinesFeatureConfig;

@TargetFeatureConfigType("twisting_vines")
public class TwistingVinesJsonParser implements FeatureJsonParser {
    private TwistingVinesFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof TwistingVinesFeatureConfig t) {
            this.config = t;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("spread_width", new JsonPrimitive(config.spreadWidth()));
        main.add("spread_height", new JsonPrimitive(config.spreadHeight()));
        main.add("max_height", new JsonPrimitive(config.maxHeight()));
        return main;
    }
}
