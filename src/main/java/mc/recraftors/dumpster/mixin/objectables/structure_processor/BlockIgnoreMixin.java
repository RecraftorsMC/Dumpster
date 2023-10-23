package mc.recraftors.dumpster.mixin.objectables.structure_processor;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.block.Block;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockIgnoreStructureProcessor.class)
public abstract class BlockIgnoreMixin implements IObjectable {
    @Shadow protected abstract StructureProcessorType<?> getType();

    @Shadow @Final private ImmutableList<Block> blocks;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.STRUCTURE_PROCESSOR.getId(getType()))));
        JsonArray array = new JsonArray();
        blocks.forEach(b -> array.add(JsonUtils.blockStateJSon(b.getDefaultState())));
        o.add("blocks", array);
        return o;
    }
}
