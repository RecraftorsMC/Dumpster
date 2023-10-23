package mc.recraftors.dumpster.mixin.objectables.block_predicate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.blockpredicate.CombinedBlockPredicate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(CombinedBlockPredicate.class)
public abstract class CombinedMixin implements BlockPredicate, IObjectable {
    @Shadow @Final protected List<BlockPredicate> predicates;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.BLOCK_PREDICATE_TYPE.getId(getType()))));
        JsonArray array = new JsonArray();
        this.predicates.forEach(p -> array.add(JsonUtils.objectJson(p)));
        o.add("predicates", array);
        return o;
    }
}
