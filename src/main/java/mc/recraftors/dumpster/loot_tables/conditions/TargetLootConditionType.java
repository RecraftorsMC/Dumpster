package mc.recraftors.dumpster.loot_tables.conditions;

import net.minecraft.loot.condition.LootCondition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetLootConditionType {
    Class<? extends LootCondition> value();

    int priority() default 1;
}
