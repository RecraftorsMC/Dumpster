package mc.recraftors.dumpster.mixin.objectables.height_provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.Objectable;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.heightprovider.HeightProviderType;
import net.minecraft.world.gen.heightprovider.WeightedListHeightProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WeightedListHeightProvider.class)
public abstract class WeightedListMixin implements IObjectable {
    @Shadow public abstract HeightProviderType<?> getType();

    @Shadow @Final private DataPool<HeightProvider> weightedList;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.HEIGHT_PROVIDER_TYPE.getId(getType()))));
        JsonArray dist = new JsonArray();
        weightedList.getEntries().forEach(e -> {
            JsonObject entry = new JsonObject();
            entry.add("data", ((Objectable)e.getData()).toJson());
            entry.add("weight", new JsonPrimitive(e.getWeight().getValue()));
            dist.add(entry);
        });
        o.add("distribution", dist);
        return o;
    }
}
