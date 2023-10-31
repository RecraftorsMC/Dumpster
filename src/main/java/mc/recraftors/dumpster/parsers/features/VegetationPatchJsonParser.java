package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.VegetationPatchFeatureConfig;

@TargetFeatureConfigType(value = "vegetation_patch", supports = "waterlogged_vegetation_patch")
public class VegetationPatchJsonParser implements FeatureJsonParser {
    private VegetationPatchFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof VegetationPatchFeatureConfig v) {
            this.config = v;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("surface", new JsonPrimitive(config.surface.asString()));
        main.add("depth", JsonUtils.objectJson(config.depth));
        main.add("vertical_range", new JsonPrimitive(config.verticalRange));
        main.add("extra_bottom_block_chance", new JsonPrimitive(config.extraBottomBlockChance));
        main.add("vegetation_chance", new JsonPrimitive(config.vegetationChance));
        main.add("xz_radius", JsonUtils.objectJson(config.horizontalRadius));
        main.add("replaceable", new JsonPrimitive("#"+config.replaceable.id()));
        main.add("ground_state", JsonUtils.objectJson(config.groundState));
        main.add("vegetation_feature", JsonUtils.jsonPlacedFeatureRegEntry(config.vegetationFeature));
        return main;
    }
}
