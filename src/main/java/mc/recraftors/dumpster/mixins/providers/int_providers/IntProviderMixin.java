package mc.recraftors.dumpster.mixins.providers.int_providers;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(IntProvider.class)
public abstract class IntProviderMixin implements IObjectable {
    @Shadow public abstract IntProviderType<?> getType();

    @Shadow public abstract int getMin();

    @Shadow public abstract int getMax();

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(Registry.INT_PROVIDER_TYPE.getId(this.getType()).toString()));
        o.add("min_inclusive", new JsonPrimitive(this.getMin()));
        o.add("max_inclusive", new JsonPrimitive(this.getMax()));
        return o;
    }
}
