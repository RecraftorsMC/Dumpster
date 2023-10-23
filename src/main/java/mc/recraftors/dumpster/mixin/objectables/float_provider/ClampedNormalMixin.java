package mc.recraftors.dumpster.mixin.objectables.float_provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.math.floatprovider.ClampedNormalFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProviderType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClampedNormalFloatProvider.class)
public abstract class ClampedNormalMixin implements IObjectable {
    @Shadow public abstract FloatProviderType<?> getType();

    @Shadow private float mean;

    @Shadow private float deviation;

    @Shadow private float min;

    @Shadow private float max;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(Registry.FLOAT_PROVIDER_TYPE.getId(getType()).toString()));
        o.add("mean", new JsonPrimitive(mean));
        o.add("deviation", new JsonPrimitive(deviation));
        o.add("min", new JsonPrimitive(min));
        o.add("max", new JsonPrimitive(max));
        return o;
    }
}
