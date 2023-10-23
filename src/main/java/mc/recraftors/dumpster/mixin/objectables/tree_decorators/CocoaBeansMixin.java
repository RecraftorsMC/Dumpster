package mc.recraftors.dumpster.mixin.objectables.tree_decorators;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.treedecorator.CocoaBeansTreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CocoaBeansTreeDecorator.class)
public abstract class CocoaBeansMixin implements IObjectable {
    @Shadow protected abstract TreeDecoratorType<?> getType();

    @Shadow @Final private float probability;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.TREE_DECORATOR_TYPE.getId(getType()))));
        o.add("probability", new JsonPrimitive(probability));
        return o;
    }
}
