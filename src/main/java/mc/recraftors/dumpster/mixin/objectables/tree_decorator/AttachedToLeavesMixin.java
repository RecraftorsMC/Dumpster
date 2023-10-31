package mc.recraftors.dumpster.mixin.objectables.tree_decorator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.treedecorator.AttachedToLeavesTreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(AttachedToLeavesTreeDecorator.class)
public abstract class AttachedToLeavesMixin implements IObjectable {
    @Shadow protected abstract TreeDecoratorType<?> getType();

    @Shadow @Final protected float probability;

    @Shadow @Final protected int exclusionRadiusXZ;

    @Shadow @Final protected int exclusionRadiusY;

    @Shadow @Final protected int requiredEmptyBlocks;

    @Shadow @Final protected BlockStateProvider blockProvider;

    @Shadow @Final protected List<Direction> directions;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.TREE_DECORATOR_TYPE.getId(getType()))));
        o.add("probability", new JsonPrimitive(probability));
        o.add("exclusion_radius_xz", new JsonPrimitive(exclusionRadiusXZ));
        o.add("exclusion_radius_y", new JsonPrimitive(exclusionRadiusY));
        o.add("required_empty_blocks", new JsonPrimitive(requiredEmptyBlocks));
        o.add("block_provider", JsonUtils.objectJson(blockProvider));
        JsonArray dir = new JsonArray(directions.size());
        directions.forEach(direction -> dir.add(direction.asString()));
        o.add("directions", dir);
        return o;
    }
}
