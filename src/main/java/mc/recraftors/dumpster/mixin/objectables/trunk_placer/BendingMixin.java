package mc.recraftors.dumpster.mixin.objectables.trunk_placer;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.trunk.BendingTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BendingTrunkPlacer.class)
public abstract class BendingMixin extends TrunkPlacer implements IObjectable {
    @Shadow protected abstract TrunkPlacerType<?> getType();

    @Shadow @Final private IntProvider bendLength;

    @Shadow @Final private int minHeightForLeaves;

    BendingMixin(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
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
        o.add("bend_length", JsonUtils.objectJson(bendLength));
        o.add("min_height_for_leaves", new JsonPrimitive(minHeightForLeaves));
        return o;
    }
}
