package mc.recraftors.dumpster.mixin.objectables.position_source;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.PositionSourceType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(EntityPositionSource.class)
public abstract class EntityPositionSourceMixin implements IObjectable {
    @Shadow public abstract PositionSourceType<?> getType();

    @Shadow protected abstract UUID getUuid();

    @Shadow @Final private float yOffset;

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(Registry.POSITION_SOURCE_TYPE.getId(getType()).getNamespace()));
        JsonArray e = new JsonArray();
        UUID u = getUuid();
        long m = u.getMostSignificantBits();
        long l = u.getLeastSignificantBits();
        e.add((int)(m >> 32));
        e.add((int)m);
        e.add((int)(l >> 32));
        e.add((int)l);
        o.add("source_entity", e);
        o.add("y_offset", new JsonPrimitive(yOffset));
        return o;
    }
}
