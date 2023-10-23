package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import net.minecraft.world.gen.feature.FeatureConfig;

@TargetFeatureConfigType(value = "no_op", supports = {
        "chorus_plant", "void_start_platform", "desert_well", "ice_spike", "glowstone_blob", "freeze_top_layer",
        "vines", "monster_room", "blue_ice", "end_island", "kelp", "coral_tree", "coral_mushroom", "coral_claw",
        "weeping_vines", "bonus_chest", "basalt_pillar"
})
public class DefaultJsonParser implements FeatureJsonParser {
    @Override
    public boolean in(FeatureConfig feature) {
        return true;
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject();
    }
}
