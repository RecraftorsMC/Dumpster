package mc.recraftors.dumpster.mixin.objectables.structure_processor;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StructureProcessor.class)
public abstract class StructureProcessorMixin implements IObjectable {
    @Shadow protected abstract StructureProcessorType<?> getType();

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.STRUCTURE_PROCESSOR.getId(getType()))));
        return o;
    }
}
