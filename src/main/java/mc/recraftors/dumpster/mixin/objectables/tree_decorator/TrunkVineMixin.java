package mc.recraftors.dumpster.mixin.objectables.tree_decorator;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraft.world.gen.treedecorator.TrunkVineTreeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TrunkVineTreeDecorator.class)
public abstract class TrunkVineMixin implements IObjectable {
    @Shadow protected abstract TreeDecoratorType<?> getType();

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.TREE_DECORATOR_TYPE.getId(getType()))));
        return o;
    }
}
