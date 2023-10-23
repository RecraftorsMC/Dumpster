package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.DripstoneClusterFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

@TargetFeatureConfigType("dripstone_cluster")
public class DripstoneClusterJsonParser implements FeatureJsonParser {
    private DripstoneClusterFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof DripstoneClusterFeatureConfig d) {
            this.config = d;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        JsonObject main = new JsonObject();
        main.add("floor_to_ceiling_search_range", new JsonPrimitive(config.floorToCeilingSearchRange));
        main.add("height", JsonUtils.objectJson(config.height));
        main.add("radius", JsonUtils.objectJson(config.radius));
        main.add("max_stalagmite_stalagtite_height_diff", new JsonPrimitive(config.maxStalagmiteStalactiteHeightDiff));
        main.add("height_deviation", new JsonPrimitive(config.heightDeviation));
        main.add("dripstone_layer_thickness", JsonUtils.objectJson(config.dripstoneBlockLayerThickness));
        main.add("density", JsonUtils.objectJson(config.density));
        main.add("wetness", JsonUtils.objectJson(config.wetness));
        main.add("chance_of_drimstone_column_at_max_distance_from_center",
                new JsonPrimitive(config.chanceOfDripstoneColumnAtMaxDistanceFromCenter));
        main.add("max_distance_from_edge_affecting_chance_of_dripstone_column",
                new JsonPrimitive(config.maxDistanceFromCenterAffectingChanceOfDripstoneColumn));
        main.add("max_distance_from_center_affecting_height_bias",
                new JsonPrimitive(config.maxDistanceFromCenterAffectingHeightBias));
        return main;
    }
}
