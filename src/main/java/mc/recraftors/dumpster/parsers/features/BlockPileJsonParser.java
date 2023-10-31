package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.Objectable;
import net.minecraft.world.gen.feature.BlockPileFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

@TargetFeatureConfigType("block_pile")
public class BlockPileJsonParser implements FeatureJsonParser {
    private BlockPileFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof BlockPileFeatureConfig b) {
            this.config = b;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (this.config == null) return null;
        JsonObject main = new JsonObject();
        main.add("provider", JsonUtils.objectJson(this.config.stateProvider));
        return main;
    }
}
