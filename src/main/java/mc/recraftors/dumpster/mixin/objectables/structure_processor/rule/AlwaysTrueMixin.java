package mc.recraftors.dumpster.mixin.objectables.structure_processor.rule;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AlwaysTrueRuleTest.class)
public abstract class AlwaysTrueMixin implements IObjectable {
    @Shadow protected abstract RuleTestType<?> getType();

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.RULE_TEST.getId(getType()))));
        return o;
    }
}
