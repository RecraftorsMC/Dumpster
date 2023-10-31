package mc.recraftors.dumpster.mixin.objectables.carver_config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import mc.recraftors.dumpster.utils.Objectable;
import mc.recraftors.dumpster.utils.accessors.IObjectable;
import net.minecraft.block.Block;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.CarverDebugConfig;
import net.minecraft.world.gen.carver.CaveCarverConfig;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CaveCarverConfig.class)
public abstract class CaveCarverConfigMixin extends CarverConfig implements IObjectable {
    @Shadow @Final public FloatProvider horizontalRadiusMultiplier;

    @Shadow @Final public FloatProvider verticalRadiusMultiplier;

    @Shadow @Final FloatProvider floorLevel;

    CaveCarverConfigMixin(float probability, HeightProvider y, FloatProvider yScale, YOffset lavaLevel, CarverDebugConfig debugConfig, RegistryEntryList<Block> replaceable) {
        super(probability, y, yScale, lavaLevel, debugConfig, replaceable);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public JsonObject dumpster$toJson() {
        JsonObject o = new JsonObject();
        o.add("probability", new JsonPrimitive(probability));
        o.add("y", ((Objectable)y).toJson());
        o.add("lava_level", ((Objectable)lavaLevel).toJson());
        JsonArray repl = new JsonArray();
        replaceable.forEach(e -> repl.add(Registry.BLOCK.getId(e.value()).toString()));
        o.add("replaceable", repl.size() == 1 ? repl.get(0) : repl);
        if (debugConfig != null) {
            JsonObject debug = new JsonObject();
            debug.add("debug_mode", new JsonPrimitive(debugConfig.isDebugMode()));
            debug.add("air_state", JsonUtils.blockStateJSon(debugConfig.getAirState()));
            debug.add("water_state", JsonUtils.blockStateJSon(debugConfig.getWaterState()));
            debug.add("lava_state", JsonUtils.blockStateJSon(debugConfig.getLavaState()));
            debug.add("barrier_state", JsonUtils.blockStateJSon(debugConfig.getBarrierState()));
            o.add("debug_settings", debug);
        }
        o.add("yScale", ((Objectable)yScale).toJson());
        o.add("horizontal_radius_multiplier", ((Objectable)horizontalRadiusMultiplier).toJson());
        o.add("vertical_radius_multiplier", ((Objectable)verticalRadiusMultiplier).toJson());
        o.add("floor_level", ((Objectable)floorLevel).toJson());
        return null;
    }
}
