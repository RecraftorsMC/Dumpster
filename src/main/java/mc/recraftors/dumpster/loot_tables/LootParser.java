package mc.recraftors.dumpster.loot_tables;

import com.google.gson.JsonObject;
import mc.recraftors.dumpster.loot_tables.functions.LootFunctionJsonParser;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;

import java.util.Map;

public final class LootParser {
    private LootParser() {}

    public static JsonObject parseTable(LootTable table, Identifier type, Map<Identifier, LootFunctionJsonParser> parserMap) {
        JsonObject main = new JsonObject();
        //TODO: this
        return main;
    }
}
