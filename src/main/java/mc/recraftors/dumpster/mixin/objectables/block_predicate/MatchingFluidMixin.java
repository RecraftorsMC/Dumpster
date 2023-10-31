package mc.recraftors.dumpster.mixin.objectables.block_predicate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.blockpredicate.MatchingFluidsBlockPredicate;
import net.minecraft.world.gen.blockpredicate.OffsetPredicate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MatchingFluidsBlockPredicate.class)
public abstract class MatchingFluidMixin extends OffsetPredicate implements IObjectable {
    @Shadow @Final private RegistryEntryList<Fluid> fluids;

    protected MatchingFluidMixin(Vec3i offset) {
        super(offset);
    }

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.BLOCK_PREDICATE_TYPE.getId(getType()))));
        o.add("offset", JsonUtils.vec3iJson(offset));
        JsonArray array = new JsonArray();
        this.fluids.forEach(f -> array.add(Registry.FLUID.getId(f.value()).toString()));
        o.add("fluids", array);
        return o;
    }
}
