package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RootSystemFeatureConfig;

@TargetFeatureConfigType("root_system")
public class RootSystemJsonParser implements FeatureJsonParser {
    private RootSystemFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof RootSystemFeatureConfig r) {
            this.config = r;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("required_vertical_space_for_tree", new JsonPrimitive(config.requiredVerticalSpaceForTree));
        main.add("root_radius", new JsonPrimitive(config.rootRadius));
        main.add("root_placement_attempts", new JsonPrimitive(config.rootPlacementAttempts));
        main.add("root_column_max_height", new JsonPrimitive(config.maxRootColumnHeight));
        main.add("hanging_root_radius", new JsonPrimitive(config.hangingRootRadius));
        main.add("hanging_roots_vertical_span", new JsonPrimitive(config.hangingRootVerticalSpan));
        main.add("hanging_root_placement_attempts", new JsonPrimitive(config.hangingRootPlacementAttempts));
        main.add("allowed_vertical_water_for_tree", new JsonPrimitive(config.allowedVerticalWaterForTree));
        main.add("root_replaceable", new JsonPrimitive("#"+config.rootReplaceable.id()));
        main.add("root_state_provider", JsonUtils.objectJson(config.rootStateProvider));
        main.add("hanging_root_state_provider", JsonUtils.objectJson(config.hangingRootStateProvider));
        main.add("allowed_tree_position", JsonUtils.objectJson(config.predicate));
        main.add("feature", JsonUtils.jsonPlacedFeatureRegEntry(config.feature));
        return main;
    }
}
