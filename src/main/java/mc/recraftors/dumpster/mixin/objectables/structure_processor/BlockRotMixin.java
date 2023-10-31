package mc.recraftors.dumpster.mixin.objectables.structure_processor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.block.Block;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.registry.RegistryEntryList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(BlockRotStructureProcessor.class)
public abstract class BlockRotMixin implements IObjectable {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Shadow private Optional<RegistryEntryList<Block>> rottableBlocks;

    @Shadow @Final private float integrity;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("integrity", new JsonPrimitive(integrity));
        JsonArray rottable = new JsonArray();
        rottableBlocks.ifPresent(e -> e.forEach(b -> rottable.add(JsonUtils.jsonBlockRegEntry(b))));
        o.add("rottable_blocks", rottable);
        return o;
    }
}
