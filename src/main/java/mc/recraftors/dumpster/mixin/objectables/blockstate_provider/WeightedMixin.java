package mc.recraftors.dumpster.mixin.objectables.blockstate_provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.block.BlockState;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WeightedBlockStateProvider.class)
public abstract class WeightedMixin implements IObjectable {
    @Shadow protected abstract BlockStateProviderType<?> getType();

    @Shadow @Final private DataPool<BlockState> states;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(getType()).toString()));
        JsonArray arr = new JsonArray();
        states.getEntries().forEach(e -> {
            JsonObject entry = new JsonObject();
            entry.add("data", JsonUtils.blockStateJSon(e.getData()));
            entry.add("weight", new JsonPrimitive(e.getWeight().getValue()));
            arr.add(entry);
        });
        o.add("entries", arr);
        return o;
    }
}
