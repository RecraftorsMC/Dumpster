package mc.recraftors.dumpster.mixin.objectables.blockstate_provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SimpleBlockStateProvider.class)
public abstract class SimpleMixin implements IObjectable {
    @Shadow protected abstract BlockStateProviderType<?> getType();

    @Shadow @Final private BlockState state;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(getType()))));
        o.add("state", JsonUtils.blockStateJSon(state));
        return o;
    }
}
