package mc.recraftors.dumpster.mixin.objectables.structure_placement;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(ConcentricRingsStructurePlacement.class)
public abstract class ConcentricRingsMixin extends StructurePlacement implements IObjectable {
    @Shadow public abstract StructurePlacementType<?> getType();

    @Shadow @Final private int distance;

    @Shadow @Final private int count;

    @Shadow @Final private RegistryEntryList<Biome> preferredBiomes;

    @Shadow @Final private int spread;

    ConcentricRingsMixin(Vec3i locateOffset, FrequencyReductionMethod frequencyReductionMethod, float frequency, int salt, Optional<ExclusionZone> exclusionZone) {
        super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.STRUCTURE_PLACEMENT.getId(getType()))));
        o.add("salt", new JsonPrimitive(getSalt()));
        o.add("frequency", new JsonPrimitive(getFrequency()));
        o.add("frequency_reduction_method", new JsonPrimitive(getFrequencyReductionMethod().asString()));
        getExclusionZone().ifPresent(zone -> {
            JsonObject exclusion = new JsonObject();
            exclusion.add("chunk_count", new JsonPrimitive(zone.chunkCount()));
            exclusion.add("other_set", JsonUtils.jsonStructureSetRegEntry(zone.otherSet()));
            o.add("exclusion_zone", exclusion);
        });
        o.add("locate_offset", JsonUtils.vec3iJson(getLocateOffset()));
        o.add("distance", new JsonPrimitive(distance));
        o.add("count", new JsonPrimitive(count));
        JsonArray biomes = new JsonArray();
        preferredBiomes.forEach(entry -> biomes.add(JsonUtils.jsonBiomeRegEntry(entry)));
        o.add("preferred_biomes", biomes);
        o.add("spread", new JsonPrimitive(spread));
        return o;
    }
}
