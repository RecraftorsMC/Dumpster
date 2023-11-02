package mc.recraftors.dumpster.parsers.registries;

import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.InResult;

@TargetRegistryType(Object.class)
public class ObjectJsonParser implements RegistryJsonParser {
    @Override
    public InResult in(Object o) {
        return InResult.SUCCESS;
    }

    @Override
    public JsonObject toJson() {
        return null;
    }
}
