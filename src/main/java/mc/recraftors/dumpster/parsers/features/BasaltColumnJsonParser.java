package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.Objectable;
import net.minecraft.world.gen.feature.BasaltColumnsFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

@TargetFeatureConfigType("basalt_columns")
public class BasaltColumnJsonParser implements FeatureJsonParser {
    private BasaltColumnsFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof BasaltColumnsFeatureConfig b) {
            this.config = b;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (config == null) return null;
        JsonObject o = new JsonObject();
        o.add("reach", ((Objectable)this.config.getReach()).toJson());
        o.add("height", ((Objectable)this.config.getHeight()).toJson());
        return o;
    }
}
