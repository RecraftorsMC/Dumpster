package mc.recraftors.dumpster.mixin.objectables.float_provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.math.floatprovider.FloatProviderType;
import net.minecraft.util.math.floatprovider.TrapezoidFloatProvider;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TrapezoidFloatProvider.class)
public abstract class TrapezoidMixin implements IObjectable {
    @Shadow public abstract FloatProviderType<?> getType();

    @Shadow @Final private float min;

    @Shadow @Final private float max;

    @Shadow @Final private float plateau;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.FLOAT_PROVIDER_TYPE.getId(getType()))));
        o.add("min", new JsonPrimitive(min));
        o.add("max", new JsonPrimitive(max));
        o.add("plateau", new JsonPrimitive(plateau));
        return o;
    }
}
