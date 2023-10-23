package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.Objectable;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

@TargetFeatureConfigType("disk")
public class DiskJsonParser implements FeatureJsonParser {
    private DiskFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof DiskFeatureConfig d) {
            this.config = d;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        JsonObject main = new JsonObject();
        JsonObject state = new JsonObject();
        state.add("fallback", JsonUtils.objectJson(config.stateProvider().fallback()));
        JsonArray rules = new JsonArray();
        config.stateProvider().rules().forEach(rule -> {
            JsonObject o = new JsonObject();
            o.add("if_true", JsonUtils.objectJson(rule.ifTrue()));
            o.add("then", JsonUtils.objectJson(rule.then()));
            rules.add(o);
        });
        state.add("rules", rules);
        main.add("state_provider", state);
        main.add("radius", ((Objectable)config.radius()).toJson());
        main.add("half_height", new JsonPrimitive(config.halfHeight()));
        main.add("target", JsonUtils.objectJson(config.target()));
        return main;
    }
}
