package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.EmeraldOreFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

@TargetFeatureConfigType(value = "emerald_ore", supports = "replace_single_block")
public class EmeraldOreJsonParser implements FeatureJsonParser {
    private EmeraldOreFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof EmeraldOreFeatureConfig e) {
            this.config = e;
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
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
