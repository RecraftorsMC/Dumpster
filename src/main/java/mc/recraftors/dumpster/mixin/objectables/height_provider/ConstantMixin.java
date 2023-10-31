package mc.recraftors.dumpster.mixin.objectables.height_provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.Objectable;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.heightprovider.HeightProviderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ConstantHeightProvider.class)
public abstract class ConstantMixin implements IObjectable {
    @Shadow public abstract HeightProviderType<?> getType();

    @Shadow @Final private YOffset offset;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.HEIGHT_PROVIDER_TYPE.getId(getType()))));
        o.add("value", ((Objectable)this.offset).toJson());
        return o;
    }
}
