package mc.recraftors.dumpster.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.Identifier;

public record RegistryEntry(Identifier id, Class<?> clazz, String s) implements Objectable {
    @Override
    public JsonObject toJson() {
        JsonObject o = new JsonObject();
        o.add("id", new JsonPrimitive(id.toString()));
        o.add("class", new JsonPrimitive(clazz.getName()));
        o.add("s", new JsonPrimitive(s));
        return o;
    }
}
