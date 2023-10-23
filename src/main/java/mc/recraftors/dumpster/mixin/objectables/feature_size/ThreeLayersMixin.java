package mc.recraftors.dumpster.mixin.objectables.feature_size;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.size.FeatureSize;
import net.minecraft.world.gen.feature.size.FeatureSizeType;
import net.minecraft.world.gen.feature.size.ThreeLayersFeatureSize;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.OptionalInt;

@Mixin(ThreeLayersFeatureSize.class)
public abstract class ThreeLayersMixin extends FeatureSize implements IObjectable {
    @Shadow protected abstract FeatureSizeType<?> getType();

    @Shadow @Final private int limit;

    @Shadow @Final private int upperLimit;

    @Shadow @Final private int lowerSize;

    @Shadow @Final private int middleSize;

    @Shadow @Final private int upperSize;

    ThreeLayersMixin(OptionalInt minClippedHeight) {
        super(minClippedHeight);
    }

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        minClippedHeight.ifPresent(v -> o.add("min_clipped_height", new JsonPrimitive(v)));
        o.add("type", new JsonPrimitive(String.valueOf(Registry.FEATURE_SIZE_TYPE.getId(getType()))));
        o.add("limit", new JsonPrimitive(limit));
        o.add("upper_limit", new JsonPrimitive(upperLimit));
        o.add("lower_size", new JsonPrimitive(lowerSize));
        o.add("middle_size", new JsonPrimitive(middleSize));
        o.add("upper_size", new JsonPrimitive(upperSize));
        return o;
    }
}
