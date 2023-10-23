package mc.recraftors.dumpster.mixin.objectables.structure_processor;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GravityStructureProcessor.class)
public abstract class GravityMixin implements IObjectable {
    @Shadow protected abstract StructureProcessorType<?> getType();

    @Shadow @Final private Heightmap.Type heightmap;

    @Shadow @Final private int offset;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.STRUCTURE_PROCESSOR.getId(getType()))));
        o.add("heightmap", new JsonPrimitive(heightmap.asString()));
        o.add("offset", new JsonPrimitive(offset));
        return o;
    }
}
