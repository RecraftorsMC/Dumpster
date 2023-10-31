package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

@TargetFeatureConfigType("tree")
public class TreeJsonParser implements FeatureJsonParser {
    private TreeFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof TreeFeatureConfig t) {
            this.config = t;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject main = new JsonObject();
        main.add("ignore_vines", new JsonPrimitive(config.ignoreVines));
        main.add("force_dirt", new JsonPrimitive(config.forceDirt));
        main.add("dirt_provider", JsonUtils.objectJson(config.dirtProvider));
        main.add("trunk_provider", JsonUtils.objectJson(config.trunkProvider));
        main.add("foliage_provider", JsonUtils.objectJson(config.foliageProvider));
        main.add("minimum_size", JsonUtils.objectJson(config.minimumSize));
        config.rootPlacer.ifPresent(placer -> main.add("root_placer", JsonUtils.objectJson(placer)));
        main.add("foliage_place", JsonUtils.objectJson(config.foliagePlacer));
        JsonArray decorators = new JsonArray(config.decorators.size());
        config.decorators.forEach(deco -> decorators.add(JsonUtils.objectJson(deco)));
        main.add("decorators", decorators);
        return main;
    }
}
