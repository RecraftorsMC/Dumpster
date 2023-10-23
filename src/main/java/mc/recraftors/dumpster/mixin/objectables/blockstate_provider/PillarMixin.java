package mc.recraftors.dumpster.mixin.objectables.blockstate_provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.stateprovider.PillarBlockStateProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PillarBlockStateProvider.class)
public abstract class PillarMixin implements IObjectable {
    @Shadow protected abstract BlockStateProviderType<?> getType();

    @Shadow @Final private Block block;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(getType()).toString()));
        o.add("state", JsonUtils.blockStateJSon(block.getDefaultState()));
        return o;
    }
}
