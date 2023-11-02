package mc.recraftors.dumpster.parsers.registries;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.InResult;
import net.minecraft.entity.attribute.EntityAttribute;

@TargetRegistryType(EntityAttribute.class)
public class EntityAttributeJsonParser implements RegistryJsonParser {
    private EntityAttribute attribute;

    @Override
    public InResult in(Object o) {
        if (o instanceof EntityAttribute a) {
            this.attribute = a;
            return InResult.SUCCESS;
        }
        return InResult.FAILURE;
    }

    @Override
    public JsonObject toJson() {
        if (attribute == null) return null;
        JsonObject o = new JsonObject();
        o.add("tracked", new JsonPrimitive(attribute.isTracked()));
        o.add("translationKey", new JsonPrimitive(attribute.getTranslationKey()));
        o.add("defaultValue", new JsonPrimitive(attribute.getDefaultValue()));
        return o;
    }
}
