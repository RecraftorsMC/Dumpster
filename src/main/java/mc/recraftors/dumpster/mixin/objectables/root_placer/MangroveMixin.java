package mc.recraftors.dumpster.mixin.objectables.root_placer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.root.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(MangroveRootPlacer.class)
public abstract class MangroveMixin extends RootPlacer implements IObjectable {
    @Shadow protected abstract RootPlacerType<?> getType();

    @Shadow @Final private MangroveRootPlacement mangroveRootPlacement;

    public MangroveMixin(IntProvider trunkOffsetY, BlockStateProvider rootProvider, Optional<AboveRootPlacement> aboveRootPlacement) {
        super(trunkOffsetY, rootProvider, aboveRootPlacement);
    }

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.ROOT_PLACER_TYPE.getId(getType()))));
        o.add("root_provider", JsonUtils.objectJson(rootProvider));
        o.add("trunk_offset_y", JsonUtils.objectJson(trunkOffsetY));
        aboveRootPlacement.ifPresent(placement -> {
            JsonObject o1 = new JsonObject();
            o1.add("above_root_provider", JsonUtils.objectJson(placement.aboveRootProvider()));
            o1.add("above_root_placement_chance", new JsonPrimitive(placement.aboveRootPlacementChance()));
            o.add("above_root_placement", o1);
        });
        o.add("max_root_width", new JsonPrimitive(mangroveRootPlacement.maxRootWidth()));
        o.add("max_root_length", new JsonPrimitive(mangroveRootPlacement.maxRootLength()));
        o.add("random_skew_cnance", new JsonPrimitive(mangroveRootPlacement.randomSkewChance()));
        JsonArray through = new JsonArray();
        mangroveRootPlacement.canGrowThrough().forEach(entry -> through.add(JsonUtils.jsonBlockRegEntry(entry)));
        o.add("can_grow_through", through);
        JsonArray muddy = new JsonArray();
        mangroveRootPlacement.muddyRootsIn().forEach(entry -> muddy.add(JsonUtils.jsonBlockRegEntry(entry)));
        o.add("muddy_roots_in", muddy);
        o.add("muddy_blocks_provider", JsonUtils.objectJson(mangroveRootPlacement.muddyRootsProvider()));
        return o;
    }
}
