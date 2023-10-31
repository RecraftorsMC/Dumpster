package mc.recraftors.dumpster.mixin.objectables.structure_processor.rule;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.structure.rule.LinearPosRuleTest;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LinearPosRuleTest.class)
public abstract class LinearPosMixin implements IObjectable {
    @Shadow protected abstract PosRuleTestType<?> getType();

    @Shadow @Final private float minChance;

    @Shadow @Final private float maxChance;

    @Shadow @Final private int minDistance;

    @Shadow @Final private int maxDistance;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.POS_RULE_TEST.getId(getType()))));
        o.add("min_chance", new JsonPrimitive(minChance));
        o.add("max_chance", new JsonPrimitive(maxChance));
        o.add("min_dist", new JsonPrimitive(minDistance));
        o.add("max_dist",new JsonPrimitive(maxDistance));
        return o;
    }
}
