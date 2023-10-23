package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.NetherForestVegetationFeatureConfig;

@TargetFeatureConfigType("nether_forest_vegetation")
public class NetherForestVegetationJsonParser implements FeatureJsonParser {
    private NetherForestVegetationFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof NetherForestVegetationFeatureConfig n) {
            this.config = n;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("state_provider", JsonUtils.objectJson(config.stateProvider));
        main.add("spread_width", new JsonPrimitive(config.spreadWidth));
        main.add("spread_height", new JsonPrimitive(config.spreadHeight));
        return main;
    }
}
