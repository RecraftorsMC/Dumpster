package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;

@TargetFeatureConfigType(value = "ore", supports = "scattered_ore")
public class OreJsonParser implements FeatureJsonParser {
    private OreFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof OreFeatureConfig o) {
            this.config = o;
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("size", new JsonPrimitive(config.size));
        main.add("discard_chance_on_air_exposure", new JsonPrimitive(config.discardOnAirChance));
        JsonArray targets = new JsonArray();
        config.targets.forEach(target -> {
            JsonObject o = new JsonObject();
            o.add("target", JsonUtils.objectJson(target.target));
            o.add("state", JsonUtils.blockStateJSon(target.state));
            targets.add(o);
        });
        main.add("targets", targets);
        return main;
    }
}
