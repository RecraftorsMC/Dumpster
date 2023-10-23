package mc.recraftors.dumpster.mixin.objectables.blockstate_provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.Objectable;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.stateprovider.RandomizedIntBlockStateProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RandomizedIntBlockStateProvider.class)
public abstract class RandomizedIntMixin implements IObjectable {
    @Shadow protected abstract BlockStateProviderType<?> getType();

    @Shadow @Final private String propertyName;

    @Shadow @Final private IntProvider values;

    @Shadow @Final private BlockStateProvider source;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(getType()).toString()));
        o.add("type", new JsonPrimitive(propertyName));
        o.add("values", ((Objectable)values).toJson());
        o.add("source", JsonUtils.objectJson(source));
        return o;
    }
}
