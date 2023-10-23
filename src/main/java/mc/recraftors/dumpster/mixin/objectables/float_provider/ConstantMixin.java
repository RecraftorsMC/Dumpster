package mc.recraftors.dumpster.mixin.objectables.float_provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProviderType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ConstantFloatProvider.class)
public abstract class ConstantMixin implements IObjectable {
    @Shadow public abstract FloatProviderType<?> getType();

    @Shadow @Final private float value;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(Registry.FLOAT_PROVIDER_TYPE.getId(getType()).toString()));
        o.add("value", new JsonPrimitive(value));
        return o;
    }
}
