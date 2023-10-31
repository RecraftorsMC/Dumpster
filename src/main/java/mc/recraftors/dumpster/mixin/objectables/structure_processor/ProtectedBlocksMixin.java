package mc.recraftors.dumpster.mixin.objectables.structure_processor;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.block.Block;
import net.minecraft.structure.processor.ProtectedBlocksStructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ProtectedBlocksStructureProcessor.class)
public abstract class ProtectedBlocksMixin implements IObjectable {
    @Shadow protected abstract StructureProcessorType<?> getType();

    @Shadow @Final public TagKey<Block> protectedBlocksTag;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.STRUCTURE_PROCESSOR.getId(getType()))));
        o.add("value", new JsonPrimitive("#"+protectedBlocksTag.id()));
        return o;
    }
}
