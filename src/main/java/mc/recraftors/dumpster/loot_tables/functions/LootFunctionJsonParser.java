package mc.recraftors.dumpster.loot_tables.functions;

import mc.recraftors.dumpster.utils.Objectable;
import net.minecraft.loot.function.LootFunction;

public interface LootFunctionJsonParser extends Objectable {
    void in(LootFunction function);
}
