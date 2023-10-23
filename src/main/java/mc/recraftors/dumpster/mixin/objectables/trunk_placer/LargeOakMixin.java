package mc.recraftors.dumpster.mixin.objectables.trunk_placer;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.trunk.LargeOakTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LargeOakTrunkPlacer.class)
public abstract class LargeOakMixin extends TrunkPlacer implements IObjectable {
    LargeOakMixin(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
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
        return o;
    }
}
