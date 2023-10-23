package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.HugeFungusFeatureConfig;

@TargetFeatureConfigType("huge_fungus")
public class HugeFungusJsonParser implements FeatureJsonParser {
    private HugeFungusFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof HugeFungusFeatureConfig h) {
            this.config = h;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("hat_state", JsonUtils.blockStateJSon(config.hatState));
        main.add("decor_state", JsonUtils.blockStateJSon(config.decorationState));
        main.add("stem_state", JsonUtils.blockStateJSon(config.stemState));
        main.add("valid_base_block", JsonUtils.blockStateJSon(config.validBaseBlock));
        main.add("planted", new JsonPrimitive(config.planted));
        // main.add("replaceable_blocks", JsonUtils.objectJson(config.replaceableBlocks)); // not 1.19
        return main;
    }
}
