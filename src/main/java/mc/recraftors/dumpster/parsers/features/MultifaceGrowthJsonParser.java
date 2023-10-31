package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MultifaceGrowthFeatureConfig;

@TargetFeatureConfigType("multiface_growth")
public class MultifaceGrowthJsonParser implements FeatureJsonParser {
    private MultifaceGrowthFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof MultifaceGrowthFeatureConfig m) {
            this.config = m;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("block", new JsonPrimitive(String.valueOf(Registry.BLOCK.getId(config.lichen))));
        main.add("search_range", new JsonPrimitive(config.searchRange));
        main.add("chance_of_spreading", new JsonPrimitive(config.spreadChance));
        main.add("can_place_on_floor", new JsonPrimitive(config.placeOnFloor));
        main.add("can_place_on_ceiling", new JsonPrimitive(config.placeOnCeiling));
        main.add("can_place_on_wall", new JsonPrimitive(config.placeOnWalls));
        JsonArray array = new JsonArray();
        config.canPlaceOn.forEach(b -> array.add(JsonUtils.jsonBlockRegEntry(b)));
        main.add("can_be_placed_on", array);
        return main;
    }
}
