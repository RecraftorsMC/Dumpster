package mc.recraftors.dumpster.mixin.objectables.int_provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ConstantIntProvider.class)
public abstract class ConstantMixin implements IObjectable {
    @Shadow public abstract IntProviderType<?> getType();

    @Shadow @Final private int value;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(Registry.INT_PROVIDER_TYPE.getId(this.getType()).toString()));
        o.add("value", new JsonPrimitive(this.value));
        return o;
    }
}
