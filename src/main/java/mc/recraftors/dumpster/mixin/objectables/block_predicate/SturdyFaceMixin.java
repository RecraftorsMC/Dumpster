package mc.recraftors.dumpster.mixin.objectables.block_predicate;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;
import net.minecraft.world.gen.blockpredicate.HasSturdyFacePredicate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HasSturdyFacePredicate.class)
public abstract class SturdyFaceMixin implements IObjectable {
    @Shadow public abstract BlockPredicateType<?> getType();

    @Shadow @Final private Vec3i offset;

    @Shadow @Final private Direction face;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.BLOCK_PREDICATE_TYPE.getId(getType()))));
        o.add("offset", JsonUtils.vec3iJson(offset));
        o.add("direction", new JsonPrimitive(face.getName()));
        return o;
    }
}
