package mc.recraftors.dumpster.parsers.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.JsonUtils;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.GeodeFeatureConfig;

import static mc.recraftors.dumpster.utils.JsonUtils.objectJson;

@TargetFeatureConfigType("geode")
public class GeodeJsonParser implements FeatureJsonParser {
    private GeodeFeatureConfig config;

    @Override
    public boolean in(FeatureConfig feature) {
        if (feature instanceof GeodeFeatureConfig g) {
            this.config = g;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (this.config == null) return null;
        JsonObject main = new JsonObject();
        JsonObject blocks = new JsonObject();
        blocks.add("filling_provider", objectJson(config.layerConfig.fillingProvider));
        blocks.add("inner_layer_provider", objectJson(config.layerConfig.innerLayerProvider));
        blocks.add("alternate_inner_layer_provider", objectJson(config.layerConfig.alternateInnerLayerProvider));
        blocks.add("middle_layer_provider", objectJson(config.layerConfig.middleLayerProvider));
        blocks.add("outer_layer_provider", objectJson(config.layerConfig.outerLayerProvider));
        JsonArray inner = new JsonArray();
        config.layerConfig.innerBlocks.forEach(b -> inner.add(JsonUtils.blockStateJSon(b)));
        blocks.add("inner_placements", inner);
        blocks.add("cannot_replace", new JsonPrimitive("#"+config.layerConfig.cannotReplace.id()));
        blocks.add("invalid_blocks", new JsonPrimitive("#"+config.layerConfig.invalidBlocks.id()));
        main.add("blocks", blocks);
        JsonObject thickness = new JsonObject();
        thickness.add("filling", new JsonPrimitive(config.layerThicknessConfig.filling));
        thickness.add("inner_layer", new JsonPrimitive(config.layerThicknessConfig.innerLayer));
        thickness.add("middle_layer", new JsonPrimitive(config.layerThicknessConfig.middleLayer));
        thickness.add("outer_layer", new JsonPrimitive(config.layerThicknessConfig.outerLayer));
        main.add("layers", thickness);
        JsonObject crack = new JsonObject();
        crack.add("generate_crack_chance", new JsonPrimitive(config.crackConfig.generateCrackChance));
        crack.add("base_crack_size", new JsonPrimitive(config.crackConfig.baseCrackSize));
        crack.add("crack_point_offset", new JsonPrimitive(config.crackConfig.crackPointOffset));
        main.add("crack", crack);
        main.add("noise_multiplier", new JsonPrimitive(config.noiseMultiplier));
        main.add("use_potential_placements_chance", new JsonPrimitive(config.usePotentialPlacementsChance));
        main.add("use_alternate_layer0_chance", new JsonPrimitive(config.useAlternateLayer0Chance));
        main.add("placements_require_layer0_alternate", new JsonPrimitive(config.placementsRequireLayer0Alternate));
        main.add("outer_wall_distance", objectJson(config.outerWallDistance));
        main.add("distribution_points", objectJson(config.distributionPoints));
        main.add("invalid_blocks_threshold", new JsonPrimitive(config.invalidBlocksThreshold));
        main.add("point_offset", objectJson(config.pointOffset));
        main.add("min_gen_offset", new JsonPrimitive(config.minGenOffset));
        main.add("max_gen_offset", new JsonPrimitive(config.maxGenOffset));
        return main;
    }
}
