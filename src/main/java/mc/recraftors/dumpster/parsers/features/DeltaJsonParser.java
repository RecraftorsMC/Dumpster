package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.Objectable;
import net.minecraft.world.gen.feature.DeltaFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

@TargetFeatureConfigType("delta_feature")
public class DeltaJsonParser implements FeatureJsonParser {
    private DeltaFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof DeltaFeatureConfig d) {
            this.config = d;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (this.config == null) return null;
        JsonObject main = new JsonObject();
        main.add("contents", JsonUtils.blockStateJSon(config.getContents()));
        main.add("rim", JsonUtils.blockStateJSon(config.getRim()));
        main.add("size", ((Objectable)config.getSize()).toJson());
        main.add("rim_size", ((Objectable)config.getRimSize()).toJson());
        return main;
    }
}
