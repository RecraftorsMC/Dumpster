package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.HugeMushroomFeatureConfig;

@TargetFeatureConfigType(value = "huge_mushroom", supports = {"huge_brown_mushroom", "huge_brown_mushroom"})
public class HugeMushroomJsonParser implements FeatureJsonParser {
    private HugeMushroomFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof HugeMushroomFeatureConfig h) {
            this.config = h;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("cap_provider", JsonUtils.objectJson(config.capProvider));
        main.add("stem_provider", JsonUtils.objectJson(config.stemProvider));
        main.add("foliage_radius", new JsonPrimitive(config.foliageRadius));
        return main;
    }
}
