package mc.recraftors.dumpster.mixin.objectables.particle_effect;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStackParticleEffect.class)
public abstract class ItemStackMixin implements IObjectable {
    @Shadow @Final private ItemStack stack;

    @Shadow public abstract ParticleType<ItemStackParticleEffect> getType();

    @Override
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("type", new JsonPrimitive(String.valueOf(Registry.PARTICLE_TYPE.getId(this.getType()))));
        JsonObject v = new JsonObject();
        v.add("id", new JsonPrimitive(Registry.ITEM.getId(stack.getItem()).toString()));
        v.add("Count", new JsonPrimitive(stack.getCount()));
        if (stack.hasNbt()) {
            v.add("tag", JsonUtils.nbtJson(stack.getNbt()));
        }
        o.add("value", v);
        return o;
    }
}
