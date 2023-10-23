package mc.recraftors.dumpster.mixin.objectables.y_offset;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.world.gen.YOffset;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(YOffset.BelowTop.class)
public abstract class BelowTopMixin implements IObjectable {
    @Shadow @Final private int offset;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("below_top", new JsonPrimitive(this.offset));
        return o;
    }
}
