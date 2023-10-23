package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.Objectable;
import net.minecraft.world.gen.feature.BlockColumnFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

public class BlockColumnJsonParser implements FeatureJsonParser {
    private BlockColumnFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof BlockColumnFeatureConfig b) {
            this.config = b;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (this.config == null) return null;
        JsonObject main = new JsonObject();
        main.add("direction", new JsonPrimitive(this.config.direction().getName()));
        main.add("allowed_placement", JsonUtils.objectJson(this.config.allowedPlacement()));
        main.add("prioritize_tip", new JsonPrimitive(this.config.prioritizeTip()));
        JsonArray layers = new JsonArray();
        this.config.layers().forEach(layer -> {
            JsonObject o = new JsonObject();
            o.add("height", ((Objectable)layer.height()).toJson());
            o.add("provider", JsonUtils.objectJson(layer.state()));
            layers.add(o);
        });
        main.add("layers", layers);
        return main;
    }
}
