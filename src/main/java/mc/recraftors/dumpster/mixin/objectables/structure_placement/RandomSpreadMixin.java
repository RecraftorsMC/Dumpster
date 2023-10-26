package mc.recraftors.dumpster.mixin.objectables.structure_placement;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(RandomSpreadStructurePlacement.class)
public abstract class RandomSpreadMixin extends StructurePlacement implements IObjectable {
    @Shadow @Final private SpreadType field_37774;

    @Shadow @Final private int field_37772;

    @Shadow @Final private int field_37773;

    RandomSpreadMixin(Vec3i locateOffset, FrequencyReductionMethod frequencyReductionMethod, float frequency, int salt, Optional<ExclusionZone> exclusionZone) {
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
        o.add("spread_type", new JsonPrimitive(field_37774.asString()));
        o.add("spacing", new JsonPrimitive(field_37772));
        o.add("separation", new JsonPrimitive(field_37773));
        return o;
    }
}
