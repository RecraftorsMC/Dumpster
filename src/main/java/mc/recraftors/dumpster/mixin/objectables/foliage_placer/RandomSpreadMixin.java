package mc.recraftors.dumpster.mixin.objectables.foliage_placer;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.RandomSpreadFoliagePlacer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RandomSpreadFoliagePlacer.class)
public abstract class RandomSpreadMixin extends FoliagePlacer implements IObjectable {

    @Shadow @Final private IntProvider foliageHeight;

    @Shadow @Final private int leafPlacementAttempts;

    RandomSpreadMixin(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.FOLIAGE_PLACER_TYPE.getId(getType()))));
        o.add("radius", JsonUtils.objectJson(radius));
        o.add("offset", JsonUtils.objectJson(offset));
        o.add("foliage_height", JsonUtils.objectJson(foliageHeight));
        o.add("leaf_placement_attempts", new JsonPrimitive(leafPlacementAttempts));
        return o;
    }
}
