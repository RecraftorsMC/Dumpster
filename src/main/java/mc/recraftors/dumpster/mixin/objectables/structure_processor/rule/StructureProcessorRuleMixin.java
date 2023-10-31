package mc.recraftors.dumpster.mixin.objectables.structure_processor.rule;

import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.rule.PosRuleTest;
import net.minecraft.structure.rule.RuleTest;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StructureProcessorRule.class)
public abstract class StructureProcessorRuleMixin implements IObjectable {
    @Shadow @Final private PosRuleTest positionPredicate;

    @Shadow @Final private RuleTest inputPredicate;

    @Shadow @Final private RuleTest locationPredicate;

    @Shadow @Final private BlockState outputState;

    @Shadow @Final private @Nullable NbtCompound outputNbt;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("position_predicate", JsonUtils.objectJson(positionPredicate));
        o.add("input_predicate", JsonUtils.objectJson(inputPredicate));
        o.add("location_predicate", JsonUtils.objectJson(locationPredicate));
        o.add("output_state", JsonUtils.blockStateJSon(outputState));
        if (outputNbt != null) {
            o.add("output_nbt", JsonUtils.nbtJson(outputNbt));
        }
        return o;
    }
}
