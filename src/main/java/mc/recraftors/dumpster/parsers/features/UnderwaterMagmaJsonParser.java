package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.UnderwaterMagmaFeatureConfig;

@TargetFeatureConfigType("underwater_magma")
public class UnderwaterMagmaJsonParser implements FeatureJsonParser {
    private UnderwaterMagmaFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof UnderwaterMagmaFeatureConfig u) {
            this.config = u;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("floor_search_range", new JsonPrimitive(config.floorSearchRange));
        main.add("placement_radius_around_floor", new JsonPrimitive(config.placementRadiusAroundFloor));
        main.add("placement_probability_per_valid_position", new JsonPrimitive(config.placementProbabilityPerValidPosition));
        return main;
    }
}
