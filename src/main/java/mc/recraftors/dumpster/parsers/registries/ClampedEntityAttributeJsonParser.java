package mc.recraftors.dumpster.parsers.registries;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.InResult;
import net.minecraft.entity.attribute.ClampedEntityAttribute;

@TargetRegistryType(value = ClampedEntityAttribute.class, addon = true)
public class ClampedEntityAttributeJsonParser implements RegistryJsonParser {
    private ClampedEntityAttribute attribute;

    @Override
    public InResult in(Object o) {
        if (o instanceof ClampedEntityAttribute a) {
            this.attribute = a;
            return InResult.SUCCESS;
        }
        return InResult.FAILURE;
    }

    @Override
    public JsonObject toJson() {
        if (attribute == null) return null;
        JsonObject o = new JsonObject();
        o.add("minValue", new JsonPrimitive(attribute.getMinValue()));
        o.add("maxValue", new JsonPrimitive(attribute.getMaxValue()));
        return o;
    }
}
