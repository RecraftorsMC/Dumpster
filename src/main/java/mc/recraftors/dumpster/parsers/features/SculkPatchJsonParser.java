package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.SculkPatchFeatureConfig;

@TargetFeatureConfigType("sculk_patch")
public class SculkPatchJsonParser implements FeatureJsonParser {
    private SculkPatchFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof SculkPatchFeatureConfig s) {
            this.config = s;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("charge_count", new JsonPrimitive(config.chargeCount()));
        main.add("amount_per_charge", new JsonPrimitive(config.amountPerCharge()));
        main.add("spread_attempts", new JsonPrimitive(config.spreadAttempts()));
        main.add("growth_rounds", new JsonPrimitive(config.growthRounds()));
        main.add("spread_rounds", new JsonPrimitive(config.spreadRounds()));
        main.add("extra_rare_growths", JsonUtils.objectJson(config.extraRareGrowths()));
        main.add("catalyst_chance", new JsonPrimitive(config.catalystChance()));
        return main;
    }
}
