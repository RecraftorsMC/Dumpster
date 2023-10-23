package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

@TargetFeatureConfigType(value = "bamboo", supports = "seagrass")
public class ProbabilityJsonParser implements FeatureJsonParser {
    private ProbabilityConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof ProbabilityConfig p) {
            this.config = p;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject o = new JsonObject();
        o.add("probability", new JsonPrimitive(this.config.probability));
        return o;
    }
}
