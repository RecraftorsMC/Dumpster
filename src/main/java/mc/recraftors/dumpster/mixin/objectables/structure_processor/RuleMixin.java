package mc.recraftors.dumpster.mixin.objectables.structure_processor;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RuleStructureProcessor.class)
public abstract class RuleMixin implements IObjectable {
    @Shadow protected abstract StructureProcessorType<?> getType();

    @Shadow @Final private ImmutableList<StructureProcessorRule> rules;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o =new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.STRUCTURE_PROCESSOR.getId(getType()))));
        JsonArray array = new JsonArray();
        rules.forEach(rule -> array.add(JsonUtils.objectJson(rule)));
        return null;
    }
}
