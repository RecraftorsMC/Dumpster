package mc.recraftors.dumpster.mixin.objectables.height_provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.Objectable;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.BiasedToBottomHeightProvider;
import net.minecraft.world.gen.heightprovider.HeightProviderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BiasedToBottomHeightProvider.class)
public abstract class BiasedToBottomMixin implements IObjectable {
    @Shadow public abstract HeightProviderType<?> getType();

    @Shadow @Final private YOffset minOffset;

    @Shadow @Final private YOffset maxOffset;

    @Shadow @Final private int inner;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(Registry.HEIGHT_PROVIDER_TYPE.getId(getType()).toString()));
        o.add("min_inclusive", ((Objectable)minOffset).toJson());
        o.add("max_inclusive", ((Objectable)maxOffset).toJson());
        o.add("inner", new JsonPrimitive(inner));
        return o;
    }
}
