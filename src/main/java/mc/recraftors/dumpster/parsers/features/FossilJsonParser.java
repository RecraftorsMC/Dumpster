package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.FossilFeatureConfig;

@TargetFeatureConfigType("fossil")
public class FossilJsonParser implements FeatureJsonParser {
    private FossilFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof FossilFeatureConfig f) {
            this.config = f;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        JsonObject main = new JsonObject();
        JsonArray structures = new JsonArray();
        config.fossilStructures.forEach(i -> structures.add(i.toString()));
        main.add("fossil_structures", structures);
        JsonArray overlay = new JsonArray();
        config.fossilStructures.forEach(i -> overlay.add(i.toString()));
        main.add("overlay_structures", overlay);
        main.add("fossil_processors", JsonUtils.jsonStructureProcessorListRegEntry(config.fossilProcessors));
        main.add("overlay_processors", JsonUtils.jsonStructureProcessorListRegEntry(config.overlayProcessors));
        main.add("max_empty_corners_allowed", new JsonPrimitive(config.maxEmptyCorners));
        return main;
    }
}
