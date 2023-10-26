package mc.recraftors.dumpster.mixin.objectables.chunk_generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IDoubleBooleanProvider;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(FlatChunkGenerator.class)
public abstract class FlatMixin implements IObjectable {
    @Shadow protected abstract Codec<? extends ChunkGenerator> getCodec();

    @Shadow @Final private FlatChunkGeneratorConfig config;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.CHUNK_GENERATOR.getId(getCodec()))));
        JsonObject settings = new JsonObject();
        JsonArray layers = new JsonArray();
        this.config.getLayers().forEach(l -> {
            JsonObject layer = new JsonObject();
            layer.add("height", new JsonPrimitive(l.getThickness()));
            layer.add("block", JsonUtils.jsonBlockRegEntry(l.getBlockState().getRegistryEntry()));
            layers.add(layer);
        });
        settings.add("layers", layers);
        Optional<RegistryKey<Biome>> biome = config.getBiome().getKey();
        settings.add("biome", new JsonPrimitive((biome.isPresent() ? biome.get().getValue() : BiomeKeys.PLAINS.getValue()).toString()));
        settings.add("lakes", new JsonPrimitive(((IDoubleBooleanProvider)config).dumpster$getBool1()));
        settings.add("features", new JsonPrimitive(((IDoubleBooleanProvider)config).dumpster$getBool2()));
        config.getStructureOverrides().ifPresent(overrides -> {
            JsonArray structures = new JsonArray();
            overrides.forEach(
                    entry -> structures.add(JsonUtils.jsonStructureSetRegEntry(entry)));
            settings.add("structure_overrides", structures);
        });
        o.add("settings", settings);
        return o;
    }
}
