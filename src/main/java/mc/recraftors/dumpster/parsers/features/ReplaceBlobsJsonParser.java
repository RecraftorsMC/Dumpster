package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.ReplaceBlobsFeatureConfig;

@TargetFeatureConfigType(value = "replace_blobs", supports = "netherrack_replace_blobs")
public class ReplaceBlobsJsonParser implements FeatureJsonParser {
    private ReplaceBlobsFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof ReplaceBlobsFeatureConfig r) {
            this.config = r;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("state", JsonUtils.blockStateJSon(config.state));
        main.add("target", JsonUtils.blockStateJSon(config.target));
        main.add("radius", JsonUtils.objectJson(config.getRadius()));
        return main;
    }
}
