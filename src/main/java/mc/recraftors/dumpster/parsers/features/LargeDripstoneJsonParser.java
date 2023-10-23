package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.LargeDripstoneFeatureConfig;

@TargetFeatureConfigType("large_dripstone")
public class LargeDripstoneJsonParser implements FeatureJsonParser {
    private LargeDripstoneFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof LargeDripstoneFeatureConfig l) {
            this.config = l;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("floor_to_ceiling_search_range", new JsonPrimitive(config.floorToCeilingSearchRange));
        main.add("column_radius", JsonUtils.objectJson(config.columnRadius));
        main.add("height_scale", JsonUtils.objectJson(config.heightScale));
        main.add("max_column_radius_to_cave_height_ratio", new JsonPrimitive(config.maxColumnRadiusToCaveHeightRatio));
        main.add("stalactite_bluntness", JsonUtils.objectJson(config.stalactiteBluntness));
        main.add("wind_speed", JsonUtils.objectJson(config.windSpeed));
        main.add("min_radius_for_wind", new JsonPrimitive(config.minRadiusForWind));
        main.add("min_bluntness_for_wind", new JsonPrimitive(config.minBluntnessForWind));
        return main;
    }
}
