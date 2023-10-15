package mc.recraftors.dumpster.mixins.objectables.int_providers;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.Objectable;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.math.intprovider.ClampedIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClampedIntProvider.class)
public abstract class ClampedMixin implements IObjectable {
    @Shadow public abstract IntProviderType<?> getType();

    @Shadow public abstract int getMin();

    @Shadow public abstract int getMax();

    @Shadow @Final private IntProvider source;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(Registry.INT_PROVIDER_TYPE.getId(this.getType()).toString()));
        o.add("min_inclusive", new JsonPrimitive(this.getMin()));
        o.add("max_inclusive", new JsonPrimitive(this.getMax()));
        o.add("source", ((Objectable)this.source).toJson());
        return o;
    }
}
