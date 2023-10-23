package mc.recraftors.dumpster.mixin.objectables.block_predicate;

import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.world.gen.blockpredicate.AlwaysTrueBlockPredicate;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AlwaysTrueBlockPredicate.class)
public abstract class AlwaysTrueMixin implements IObjectable {
    @Override
    public JsonObject dumpster$toJson() {
        return new JsonObject();
    }
}
