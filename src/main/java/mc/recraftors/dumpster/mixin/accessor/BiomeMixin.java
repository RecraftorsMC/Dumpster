package mc.recraftors.dumpster.mixin.accessor;

import mc.recraftors.dumpster.utils.accessors.BiomeWeatherAccessor;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Biome.class)
public abstract class BiomeMixin implements BiomeWeatherAccessor {
    @Shadow @Final private Biome.Weather weather;

    @Override
    public Biome.Weather dumpster$getWeather() {
        return weather;
    }
}
