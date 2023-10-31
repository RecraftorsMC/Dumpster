package mc.recraftors.dumpster.mixin.objectables.trunk_placer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.block.Block;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.UpwardsBranchingTrunkPlacer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(UpwardsBranchingTrunkPlacer.class)
public abstract class UpwardsBranchingMixin extends TrunkPlacer implements IObjectable {
    @Shadow @Final private IntProvider extraBranchSteps;

    @Shadow @Final private IntProvider extraBranchLength;

    @Shadow @Final private float placeBranchPerLogProbability;

    @Shadow @Final private RegistryEntryList<Block> canGrowThrough;

    UpwardsBranchingMixin(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.TRUNK_PLACER_TYPE.getId(getType()))));
        o.add("base_height", new JsonPrimitive(baseHeight));
        o.add("height_rand_a", new JsonPrimitive(firstRandomHeight));
        o.add("height_rand_b", new JsonPrimitive(secondRandomHeight));
        o.add("extra_branch_steps", JsonUtils.objectJson(extraBranchSteps));
        o.add("extra_branch_length", JsonUtils.objectJson(extraBranchLength));
        o.add("place_branch_per_log_probability", new JsonPrimitive(placeBranchPerLogProbability));
        JsonArray through = new JsonArray();
        canGrowThrough.forEach(entry -> through.add(JsonUtils.jsonBlockRegEntry(entry)));
        o.add("can_grow_through", through);
        return o;
    }
}
