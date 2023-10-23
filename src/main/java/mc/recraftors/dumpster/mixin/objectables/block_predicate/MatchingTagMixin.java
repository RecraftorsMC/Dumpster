package mc.recraftors.dumpster.mixin.objectables.block_predicate;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockpredicate.MatchingBlockTagPredicate;
import net.minecraft.world.gen.blockpredicate.OffsetPredicate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MatchingBlockTagPredicate.class)
public abstract class MatchingTagMixin extends OffsetPredicate implements IObjectable {
    @Shadow @Final TagKey<Block> tag;

    protected MatchingTagMixin(Vec3i offset) {
        super(offset);
    }

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(Registry.BLOCK_PREDICATE_TYPE.getId(getType()).toString()));
        o.add("offset", JsonUtils.vec3iJson(offset));
        o.add("tag", new JsonPrimitive(this.tag.id().toString()));
        return o;
    }
}
