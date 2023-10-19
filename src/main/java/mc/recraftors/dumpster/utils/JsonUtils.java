package mc.recraftors.dumpster.utils;

import com.google.gson.*;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.nbt.*;
import net.minecraft.world.dimension.DimensionType;

import java.util.Iterator;
import java.util.Map;

public final class JsonUtils {
    private JsonUtils() {}

    /**
     * Generates a JSON equivalent for the provided Nbt element.
     * @param nbt The Nbt to create a Json element for
     * @return The generated Json element matching the provided Nbt.
     */
    public static JsonElement asJson(NbtElement nbt) {
        if (nbt == null) {
            return JsonNull.INSTANCE;
        }
        if (nbt instanceof NbtString s) return new JsonPrimitive(s.asString());
        if (nbt instanceof NbtByte b) return new JsonPrimitive(b.byteValue() == 1);
        if (nbt instanceof AbstractNbtNumber n) return new JsonPrimitive(n.numberValue());
        if (nbt instanceof AbstractNbtList<?> l) {
            JsonArray arr =  new JsonArray();
            l.stream().map(JsonUtils::asJson).forEach(arr::add);
            return arr;
        }
        if (nbt instanceof NbtCompound c) {
            JsonObject o = new JsonObject();
            c.getKeys().forEach(k -> o.add(k, asJson(c.get(k))));
            return o;
        }
        return null;
    }

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

    public static JsonObject dimensionJson(DimensionType dim) {
        JsonObject main = new JsonObject();
        main.add("ultrawarm", new JsonPrimitive(dim.ultrawarm()));
        main.add("natural", new JsonPrimitive(dim.natural()));
        main.add("coordinate_scale", new JsonPrimitive(dim.coordinateScale()));
        main.add("has_skylight", new JsonPrimitive(dim.hasSkyLight()));
        main.add("has_ceiling", new JsonPrimitive(dim.hasCeiling()));
        main.add("ambient_light", new JsonPrimitive(dim.ambientLight()));
        if (dim.fixedTime().isPresent()) main.add("fixed_time", new JsonPrimitive(dim.fixedTime().getAsLong()));
        main.add("monster_spawn_light_level", ((Objectable)dim.monsterSpawnLightTest()).toJson());
        main.add("monster_spawn_block_light_limit", new JsonPrimitive(dim.monsterSpawnBlockLightLimit()));
        main.add("piglin_safe", new JsonPrimitive(dim.piglinSafe()));
        main.add("bed_works", new JsonPrimitive(dim.bedWorks()));
        main.add("respawn_anchor_works", new JsonPrimitive(dim.respawnAnchorWorks()));
        main.add("has_raids", new JsonPrimitive(dim.hasRaids()));
        main.add("logical_height", new JsonPrimitive(dim.logicalHeight()));
        main.add("min_y", new JsonPrimitive(dim.minY()));
        main.add("height", new JsonPrimitive(dim.height()));
        main.add("infiniburn", new JsonPrimitive(dim.infiniburn().id().toString()));
        main.add("effects", new JsonPrimitive(dim.effects().toString()));
        return main;
    }
}
