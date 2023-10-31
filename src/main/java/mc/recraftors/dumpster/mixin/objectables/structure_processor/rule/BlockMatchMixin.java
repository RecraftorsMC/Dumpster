package mc.recraftors.dumpster.mixin.objectables.structure_processor.rule;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.block.Block;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockMatchRuleTest.class)
public abstract class BlockMatchMixin implements IObjectable {
    @Shadow protected abstract RuleTestType<?> getType();

    @Shadow @Final private Block block;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.RULE_TEST.getId(getType()))));
        o.add("block", new JsonPrimitive(String.valueOf(Registry.BLOCK.getId(block))));
        return o;
    }
}
