package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

@TargetFeatureConfigType("end_gateway")
public class EndGatewayJsonParser implements FeatureJsonParser {
    private EndGatewayFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof EndGatewayFeatureConfig e) {
            this.config = e;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        JsonObject main = new JsonObject();
        main.add("exact", new JsonPrimitive(config.isExact()));
        config.getExitPos().ifPresent(pos -> main.add("exit", JsonUtils.jsonBlockPos(pos)));
        return main;
    }
}
