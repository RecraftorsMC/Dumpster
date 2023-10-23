package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.LakeFeature;

@TargetFeatureConfigType("lake")
public class LakeJsonParser implements FeatureJsonParser {
    private LakeFeature.Config config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof LakeFeature.Config l) {
            this.config = l;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("fluid", JsonUtils.objectJson(config.fluid()));
        main.add("barrier", JsonUtils.objectJson(config.barrier()));
        return main;
    }
}
