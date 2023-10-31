package mc.recraftors.dumpster.mixin.objectables.structure_processor.rule;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.block.Block;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TagMatchRuleTest.class)
public abstract class TagMatchMixin implements IObjectable {
    @Shadow protected abstract RuleTestType<?> getType();

    @Shadow @Final private TagKey<Block> tag;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.RULE_TEST.getId(getType()))));
        o.add("tag", new JsonPrimitive(tag.id().toString()));
        return o;
    }
}
