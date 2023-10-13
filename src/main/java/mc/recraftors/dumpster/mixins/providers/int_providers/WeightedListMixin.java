package mc.recraftors.dumpster.mixins.providers.int_providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.Objectable;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.util.math.intprovider.WeightedListIntProvider;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WeightedListIntProvider.class)
public abstract class WeightedListMixin implements IObjectable {
    @Shadow public abstract IntProviderType<?> getType();

    @Shadow public abstract int getMin();

    @Shadow public abstract int getMax();

    @Shadow @Final private DataPool<IntProvider> weightedList;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(Registry.INT_PROVIDER_TYPE.getId(this.getType()).toString()));
        JsonArray arr = new JsonArray();
        this.weightedList.getEntries().forEach(p -> {
            JsonObject o2 = new JsonObject();
            o2.add("data", ((Objectable)p.getData()).toJson());
            o2.add("weight", new JsonPrimitive(p.getWeight().getValue()));
            arr.add(o2);
        });
        o.add("distribution", arr);
        return o;
    }
}
