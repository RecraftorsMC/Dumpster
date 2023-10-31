package mc.recraftors.dumpster.mixin.accessor;

import mc.recraftors.dumpster.utils.accessors.IArrayProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.SpawnSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.Map;

@Mixin(SpawnSettings.class)
public class SpawnSettingsMixin implements IArrayProvider<EntityType<?>> {
    @Shadow @Final private Map<EntityType<?>, SpawnSettings.SpawnDensity> spawnCosts;

    @Override
    public Collection<EntityType<?>> dumpster$getArray() {
        return spawnCosts.keySet();
    }
}
