package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.SpringFeatureConfig;

@TargetFeatureConfigType("spring_feature")
public class SpringJsonParser implements FeatureJsonParser {
    private SpringFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof SpringFeatureConfig s) {
            this.config = s;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("state", JsonUtils.fluidStateJson(config.state));
        main.add("rick_count", new JsonPrimitive(config.rockCount));
        main.add("hole_count", new JsonPrimitive(config.holeCount));
        main.add("requires_block_below", new JsonPrimitive(config.requiresBlockBelow));
        JsonArray valid = new JsonArray();
        config.validBlocks.forEach(entry -> valid.add(JsonUtils.jsonBlockRegEntry(entry)));
        main.add("valid_blocks", valid);
        return main;
    }
}
