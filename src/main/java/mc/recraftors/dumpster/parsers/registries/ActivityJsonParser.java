package mc.recraftors.dumpster.parsers.registries;

import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.InResult;
import net.minecraft.entity.ai.brain.Activity;

@TargetRegistryType(Activity.class)
public class ActivityJsonParser implements RegistryJsonParser {

    @Override
    public InResult in(Object o) {
        if (o instanceof Activity) {
            return InResult.SUCCESS;
        }
        return InResult.FAILURE;
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject();
    }
}
