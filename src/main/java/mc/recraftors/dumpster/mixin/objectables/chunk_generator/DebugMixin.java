package mc.recraftors.dumpster.mixin.objectables.chunk_generator;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DebugChunkGenerator.class)
public abstract class DebugMixin implements IObjectable {
    @Shadow protected abstract Codec<? extends ChunkGenerator> getCodec();

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.CHUNK_GENERATOR.getId(getCodec()))));
        return o;
    }
}
