package mc.recraftors.dumpster.mixin.objectables.block_predicate;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockpredicate.OffsetPredicate;
import net.minecraft.world.gen.blockpredicate.SolidBlockPredicate;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SolidBlockPredicate.class)
public abstract class SolidMixin extends OffsetPredicate implements IObjectable {
    protected SolidMixin(Vec3i offset) {
        super(offset);
    }

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.BLOCK_PREDICATE_TYPE.getId(getType()))));
        o.add("offset", JsonUtils.vec3iJson(offset));
        return o;
    }
}
