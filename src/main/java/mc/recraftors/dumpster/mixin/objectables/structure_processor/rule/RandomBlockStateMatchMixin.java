package mc.recraftors.dumpster.mixin.objectables.structure_processor.rule;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RandomBlockStateMatchRuleTest;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RandomBlockStateMatchRuleTest.class)
public abstract class RandomBlockStateMatchMixin implements IObjectable {
    @Shadow protected abstract RuleTestType<?> getType();

    @Shadow @Final private BlockState blockState;

    @Shadow @Final private float probability;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.RULE_TEST.getId(getType()))));
        o.add("block_state", JsonUtils.blockStateJSon(blockState));
        o.add("probability", new JsonPrimitive(probability));
        return o;
    }
}
