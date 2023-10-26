package mc.recraftors.dumpster.mixin.objectables.chunk_generator;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(NoiseChunkGenerator.class)
public abstract class NoiseMixin extends ChunkGenerator implements IObjectable {
    @Shadow protected abstract Codec<? extends ChunkGenerator> getCodec();

    NoiseMixin(Registry<StructureSet> structureSetRegistry, Optional<RegistryEntryList<StructureSet>> structureOverrides, BiomeSource biomeSource) {
        super(structureSetRegistry, structureOverrides, biomeSource);
    }

    @Shadow @Final protected RegistryEntry<ChunkGeneratorSettings> settings;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.CHUNK_GENERATOR.getId(getCodec()))));
        o.add("settings", JsonUtils.jsonChunkGeneratorSettingsRegEntry(settings));
        o.add("biome_source", JsonUtils.biomeSourceJson(biomeSource));
        return o;
    }
}
