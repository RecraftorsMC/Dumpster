package mc.recraftors.dumpster.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;

import java.util.Iterator;
import java.util.Map;

public final class JsonUtils {
    private JsonUtils() {}

    /**
     * Clears all {@code null} values or instances of {@code JsonNull} in the
     * provided JsonElement.
     * @param e The element to clear of all null values. Will be modified.
     */
    public static void jsonClearNull(JsonElement e) {
        if (e == null || !(e.isJsonArray() || e.isJsonObject())) {
            return;
        }
        if (e.isJsonObject()) {
            Iterator<Map.Entry<String, JsonElement>> iter = e.getAsJsonObject().entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, JsonElement> c = iter.next();
                if (c.getValue() == null || c.getValue().isJsonNull()) iter.remove();
                else jsonClearNull(c.getValue());
            }
        } else if (e.isJsonArray()) {
            Iterator<JsonElement> iter = e.getAsJsonArray().iterator();
            while (iter.hasNext()) {
                JsonElement c = iter.next();
                if (c == null || c.isJsonNull()) iter.remove();
                else jsonClearNull(c);
            }
        }
    }

    public static JsonObject advancementToJson(Advancement adv) {
        JsonObject main = new JsonObject();
        if (adv.getParent() != null) {
            main.add("parent", new JsonPrimitive(adv.getParent().getId().toString()));
        }
        if (adv.getDisplay() != null) {
            main.add("display", adv.getDisplay().toJson());
        }
        if (adv.getRewards() != null) {
            main.add("rewards", adv.getRewards().toJson());
        }
        JsonObject cri = new JsonObject();
        for (Map.Entry<String, AdvancementCriterion> e : adv.getCriteria().entrySet()) {
            cri.add(e.getKey(), e.getValue().toJson());
        }
        main.add("criteria", cri);
        JsonArray req = new JsonArray(adv.getRequirementCount());
        for (String[] r : adv.getRequirements()) {
            JsonArray reqX = new JsonArray(r.length);
            for (String s : r) reqX.add(s);
            req.add(reqX);
        }
        main.add("requirements", req);
        return main;
    }
}
