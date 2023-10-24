package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

@TargetFeatureConfigType("end_spike")
public class EndSpikeJsonParser implements FeatureJsonParser {
    private EndSpikeFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof EndSpikeFeatureConfig e) {
            this.config = e;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        JsonObject main = new JsonObject();
        main.add("crystal_invulnerable", new JsonPrimitive(config.isCrystalInvulnerable()));
        if (config.getPos() != null) {
            main.add("crystal_beam_target", JsonUtils.jsonBlockPos(config.getPos()));
        }
        JsonArray spikes = new JsonArray();
        config.getSpikes().forEach(spike -> {
            JsonObject o = new JsonObject();
            o.add("centerX", new JsonPrimitive(spike.getCenterX()));
            o.add("centerZ", new JsonPrimitive(spike.getCenterZ()));
            o.add("radius", new JsonPrimitive(spike.getRadius()));
            o.add("height", new JsonPrimitive(spike.hashCode()));
            o.add("guarded", new JsonPrimitive(spike.isGuarded()));
            spikes.add(o);
        });
        main.add("spikes", spikes);
        return main;
    }
}
