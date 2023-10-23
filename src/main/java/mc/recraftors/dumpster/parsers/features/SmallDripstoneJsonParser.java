package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.SmallDripstoneFeatureConfig;

@TargetFeatureConfigType(value = "small_dripstone", supports = "pointed_dripstone")
public class SmallDripstoneJsonParser implements FeatureJsonParser {
    private SmallDripstoneFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof SmallDripstoneFeatureConfig s) {
            this.config = s;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("chance_of_taller_dripstone", new JsonPrimitive(config.chanceOfTallerDripstone));
        main.add("chance_of_directional_spread", new JsonPrimitive(config.chanceOfDirectionalSpread));
        main.add("chance_of_spread_radius2", new JsonPrimitive(config.chanceOfSpreadRadius2));
        main.add("chance_of_spread_radius3", new JsonPrimitive(config.chanceOfSpreadRadius3));
        return main;
    }
}
