package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.FillLayerFeatureConfig;

@TargetFeatureConfigType("fill_layer")
public class FillLayerJsonParser implements FeatureJsonParser {
    private FillLayerFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof FillLayerFeatureConfig f) {
            this.config = f;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("state", JsonUtils.blockStateJSon(config.state));
        main.add("height", new JsonPrimitive(config.height));
        return main;
    }
}
