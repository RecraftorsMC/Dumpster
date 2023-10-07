package mc.recraftors.dumpster.mixins.accessor;

import mc.recraftors.dumpster.utils.accessors.LootFunctionsAccessor;
import mc.recraftors.dumpster.utils.accessors.LootTablePoolsAccessor;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.function.LootFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LootTable.class)
public abstract class LootTableMixin implements LootFunctionsAccessor, LootTablePoolsAccessor {
    @Shadow @Final LootFunction[] functions;

    @Shadow @Final LootPool[] pools;

    @Override
    public LootFunction[] dumster$getFunctions() {
        return this.functions;
    }

    @Override
    public LootPool[] dumpster$getPools() {
        return this.pools;
    }
}
