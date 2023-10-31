package mc.recraftors.dumpster.mixin.objectables.structure_processor.rule;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.structure.rule.AlwaysTruePosRuleTest;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AlwaysTruePosRuleTest.class)
public abstract class AlwaysTruePosMixin implements IObjectable {
    @Shadow protected abstract PosRuleTestType<?> getType();

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.POS_RULE_TEST.getId(getType()))));
        return o;
    }
}
