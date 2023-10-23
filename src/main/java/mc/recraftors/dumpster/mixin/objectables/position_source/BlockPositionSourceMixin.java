package mc.recraftors.dumpster.mixin.objectables.position_source;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.PositionSourceType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockPositionSource.class)
public abstract class BlockPositionSourceMixin implements IObjectable {
    @Shadow public abstract PositionSourceType<?> getType();

    @Shadow @Final BlockPos pos;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(Registry.POSITION_SOURCE_TYPE.getId(getType()).getNamespace()));
        JsonArray p = new JsonArray();
        p.add(pos.getX());
        p.add(pos.getY());
        p.add(pos.getZ());
        o.add("pos", p);
        return o;
    }
}
