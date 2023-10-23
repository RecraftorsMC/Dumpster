package mc.recraftors.dumpster.mixin.objectables.y_offset;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.world.gen.YOffset;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(YOffset.Fixed.class)
public abstract class FixedMixin implements IObjectable {
    @Shadow @Final private int y;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("absolute", new JsonPrimitive(this.y));
        return o;
    }
}
