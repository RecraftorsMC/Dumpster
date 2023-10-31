package mc.recraftors.dumpster.utils;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import mc.recraftors.dumpster.utils.accessors.BiomeWeatherAccessor;
import mc.recraftors.dumpster.utils.accessors.IArrayProvider;
import mc.recraftors.dumpster.utils.accessors.IBooleanProvider;
import mc.recraftors.dumpster.utils.accessors.IFloatProvider;
import mc.recraftors.dumpster.parsers.carvers.CarverJsonParser;
import mc.recraftors.dumpster.parsers.carvers.TargetCarverConfigType;
import mc.recraftors.dumpster.parsers.features.FeatureJsonParser;
import mc.recraftors.dumpster.parsers.features.TargetFeatureConfigType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.noise.NoiseRouter;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class JsonUtils {
    private JsonUtils() {}
    public static final String C_SOUND = "sound";
    private static final String C_TEMP = "temperature";

    private static final Map<Class<? extends CarverConfig>,CarverJsonParser> CARVER_PARSERS;
    private static final Map<Identifier, FeatureJsonParser> FEATURE_PARSERS;

    static {
        Map<Class<? extends CarverConfig>,CarverJsonParser> carverMap = new HashMap<>();
        FabricLoader.getInstance().getEntrypoints("carver-dump", CarverJsonParser.class).forEach(parser -> {
            if (!parser.getClass().isAnnotationPresent(TargetCarverConfigType.class)) return;
            TargetCarverConfigType type = parser.getClass().getAnnotation(TargetCarverConfigType.class);
            Class<? extends  CarverConfig> c = type.value();
            if (c == null) return;
            if (carverMap.containsKey(c)) {
                CarverJsonParser p = carverMap.get(c);
                if (p == null || !p.getClass().isAnnotationPresent(TargetCarverConfigType.class) ||
                        p.getClass().getAnnotation(TargetCarverConfigType.class).priority() < type.priority()) {
                    carverMap.put(c, parser);
                }
            } else {
                carverMap.put(c, parser);
            }
        });
        CARVER_PARSERS = carverMap;
        Map<Identifier, FeatureJsonParser> featureMap = new HashMap<>();
        FabricLoader.getInstance().getEntrypoints("feature-dump", FeatureJsonParser.class).forEach(parser -> {
            if (!parser.getClass().isAnnotationPresent(TargetFeatureConfigType.class)) return;
            TargetFeatureConfigType type = parser.getClass().getAnnotation(TargetFeatureConfigType.class);
            Identifier id = Identifier.tryParse(type.value());
            if (id == null) return;
            if (featureMap.containsKey(id)) {
                FeatureJsonParser f = featureMap.get(id);
                if (f == null || !f.getClass().isAnnotationPresent(TargetFeatureConfigType.class) ||
                        f.getClass().getAnnotation(TargetFeatureConfigType.class).priority() < type.priority()) {
                    featureMap.put(id, parser);
                }
            } else {
                featureMap.put(id, parser);
            }
        });
        FEATURE_PARSERS = featureMap;
    }

    static @Nullable FeatureJsonParser resolveFeatureParser(ConfiguredFeature<?,?> feature, Identifier id, @NotNull FeatureJsonParser parser) {
        if (!parser.getClass().isAnnotationPresent(TargetFeatureConfigType.class)) return null;
        TargetFeatureConfigType type = parser.getClass().getAnnotation(TargetFeatureConfigType.class);
        for (String s : type.supports()) {
            if (id.equals(Identifier.tryParse(s)) && parser.in(feature.config())) {
                return parser;
            }
        }
        return null;
    }

    static @Nullable FeatureJsonParser getFeatureParser(ConfiguredFeature<?,?> feature) {
        Identifier id = BuiltinRegistries.CONFIGURED_FEATURE.getId(feature);
        if (id == null) return null;
        FeatureJsonParser parser = FEATURE_PARSERS.get(id);
        if (parser == null) {
            for (FeatureJsonParser p : FEATURE_PARSERS.values()) {
                parser = resolveFeatureParser(feature, id, p);
                if (parser != null) break;
            }
        }
        return parser;
    }

    /**
     * Generates a JSON equivalent for the provided Nbt element.
     * @param nbt The Nbt to create a Json element for
     * @return The generated Json element matching the provided Nbt.
     */
    public static @Nullable JsonElement nbtJson(NbtElement nbt) {
        if (nbt == null) {
            return JsonNull.INSTANCE;
        }
        if (nbt instanceof NbtString s) return new JsonPrimitive(s.asString());
        if (nbt instanceof NbtByte b) return new JsonPrimitive(b.byteValue() == 1);
        if (nbt instanceof AbstractNbtNumber n) return new JsonPrimitive(n.numberValue());
        if (nbt instanceof AbstractNbtList<?> l) {
            JsonArray arr =  new JsonArray();
            l.stream().map(JsonUtils::nbtJson).forEach(arr::add);
            return arr;
        }
        if (nbt instanceof NbtCompound c) {
            JsonObject o = new JsonObject();
            c.getKeys().forEach(k -> o.add(k, nbtJson(c.get(k))));
            return o;
        }
        return null;
    }

    public static @NotNull JsonArray vec3iJson(@NotNull Vec3i vec) {
        JsonArray a = new JsonArray();
        a.add(vec.getX());
        a.add(vec.getY());
        a.add(vec.getZ());
        return a;
    }

    public static @NotNull JsonArray jsonBlockPos(@NotNull BlockPos pos) {
        return vec3iJson(new Vec3i(pos.getX(), pos.getY(), pos.getZ()));
    }

    public static @NotNull JsonObject jsonNoise(@NotNull DoublePerlinNoiseSampler.NoiseParameters noise) {
        JsonObject o = new JsonObject();
        o.add("firstOctave", new JsonPrimitive(noise.firstOctave()));
        JsonArray a = new JsonArray();
        noise.amplitudes().forEach(a::add);
        o.add("amplitudes", a);
        return o;
    }

    /**
     * Clears all {@code null} values or instances of {@code JsonNull} in the
     * provided JsonElement.
     * @param e The element to clear of all null values. Will be modified.
     */
    public static void jsonClearNull(JsonElement e) {
        if (e == null || !(e.isJsonArray() || e.isJsonObject())) {
            return;
        }
        if (e.isJsonObject()) {
            Iterator<Map.Entry<String, JsonElement>> iter = e.getAsJsonObject().entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, JsonElement> c = iter.next();
                if (c.getValue() == null || c.getValue().isJsonNull()) iter.remove();
                else jsonClearNull(c.getValue());
            }
        } else if (e.isJsonArray()) {
            Iterator<JsonElement> iter = e.getAsJsonArray().iterator();
            while (iter.hasNext()) {
                JsonElement c = iter.next();
                if (c == null || c.isJsonNull()) iter.remove();
                else jsonClearNull(c);
            }
        }
    }

    public static @NotNull JsonObject unknownJson(@NotNull Object o) {
        JsonObject obj = new JsonObject();
        obj.add("type", new JsonPrimitive("unknown"));
        obj.add("class", new JsonPrimitive(o.getClass().getName()));
        return obj;
    }

    public static @NotNull JsonObject objectJson(Object o) {
        try {
            return ((Objectable)o).toJson();
        } catch (ClassCastException e) {
            return unknownJson(o);
        }
    }

    static void addStateProperties(@NotNull State<?,?> state, JsonObject object) {
        Collection<Property<?>> properties = state.getProperties();
        if (!properties.isEmpty()) {
            JsonObject props = new JsonObject();
            properties.forEach(p -> props.add(p.getName(), new JsonPrimitive(state.get(p).toString())));
            object.add("Properties", props);
        }
    }

    public static @NotNull JsonObject blockStateJSon(@NotNull BlockState state) {
        JsonObject main = new JsonObject();
        main.add("Name", new JsonPrimitive(String.valueOf(Registry.BLOCK.getId(state.getBlock()))));
        addStateProperties(state, main);
        return main;
    }

    public static @NotNull JsonObject fluidStateJson(@NotNull FluidState state) {
        JsonObject main = new JsonObject();
        main.add("Name", new JsonPrimitive(String.valueOf(Registry.FLUID.getId(state.getFluid()))));
        addStateProperties(state, main);
        return main;
    }

    public static @NotNull JsonObject advancementToJson(@NotNull Advancement adv) {
        JsonObject main = new JsonObject();
        if (adv.getParent() != null) {
            main.add("parent", new JsonPrimitive(adv.getParent().getId().toString()));
        }
        if (adv.getDisplay() != null) {
            main.add("display", adv.getDisplay().toJson());
        }
        if (adv.getRewards() != null) {
            main.add("rewards", adv.getRewards().toJson());
        }
        JsonObject cri = new JsonObject();
        for (Map.Entry<String, AdvancementCriterion> e : adv.getCriteria().entrySet()) {
            cri.add(e.getKey(), e.getValue().toJson());
        }
        main.add("criteria", cri);
        JsonArray req = new JsonArray(adv.getRequirementCount());
        for (String[] r : adv.getRequirements()) {
            JsonArray reqX = new JsonArray(r.length);
            for (String s : r) reqX.add(s);
            req.add(reqX);
        }
        main.add("requirements", req);
        return main;
    }

    public static JsonObject structureJson(Structure structure) {
        Optional<JsonElement> e = Structure.STRUCTURE_CODEC.encodeStart(JsonOps.INSTANCE, structure).result();
        if (e.isEmpty() || !e.get().isJsonObject()) return unknownJson(e);
        return e.get().getAsJsonObject();
    }

    public static @NotNull JsonObject structureSetJson(@NotNull StructureSet set) {
        JsonObject main = new JsonObject();
        JsonObject structures = new JsonObject();
        set.structures().forEach(entry -> {
            JsonObject structure = new JsonObject();
            structure.add("structure", jsonStructureRegEntry(entry.structure()));
        });
        main.add("structures", structures);
        main.add("placement", objectJson(set.placement()));
        return main;
    }

    public static @NotNull JsonObject dimensionJson(@NotNull DimensionOptions dimension) {
        JsonObject main = new JsonObject();
        main.add("type", jsonDimensionTypeRegEntry(dimension.getDimensionTypeEntry()));
        main.add("generator", objectJson(dimension.getChunkGenerator()));
        return main;
    }

    public static @NotNull JsonObject dimensionTypeJson(@NotNull DimensionType dim) {
        JsonObject main = new JsonObject();
        main.add("ultrawarm", new JsonPrimitive(dim.ultrawarm()));
        main.add("natural", new JsonPrimitive(dim.natural()));
        main.add("coordinate_scale", new JsonPrimitive(dim.coordinateScale()));
        main.add("has_skylight", new JsonPrimitive(dim.hasSkyLight()));
        main.add("has_ceiling", new JsonPrimitive(dim.hasCeiling()));
        main.add("ambient_light", new JsonPrimitive(dim.ambientLight()));
        if (dim.fixedTime().isPresent()) main.add("fixed_time", new JsonPrimitive(dim.fixedTime().getAsLong()));
        main.add("monster_spawn_light_level", ((Objectable)dim.monsterSpawnLightTest()).toJson());
        main.add("monster_spawn_block_light_limit", new JsonPrimitive(dim.monsterSpawnBlockLightLimit()));
        main.add("piglin_safe", new JsonPrimitive(dim.piglinSafe()));
        main.add("bed_works", new JsonPrimitive(dim.bedWorks()));
        main.add("respawn_anchor_works", new JsonPrimitive(dim.respawnAnchorWorks()));
        main.add("has_raids", new JsonPrimitive(dim.hasRaids()));
        main.add("logical_height", new JsonPrimitive(dim.logicalHeight()));
        main.add("min_y", new JsonPrimitive(dim.minY()));
        main.add("height", new JsonPrimitive(dim.height()));
        main.add("infiniburn", new JsonPrimitive(dim.infiniburn().id().toString()));
        main.add("effects", new JsonPrimitive(dim.effects().toString()));
        return main;
    }

    public static @NotNull JsonObject soundJson(@NotNull SoundEvent e) {
        JsonObject main = new JsonObject();
        main.add("type", new JsonPrimitive(e.getId().toString()));
        if (((IBooleanProvider)e).dumpster$getBool()) {
            main.add("range", new JsonPrimitive(((IFloatProvider)e).dumpster$getFloat()));
        }
        return main;
    }

    public static JsonObject configuredFeatureJson(ConfiguredFeature<?,?> feature) {
        FeatureJsonParser parser = getFeatureParser(feature);
        return parser == null ? unknownJson(feature) : parser.toJson();
    }

    public static @NotNull JsonObject placedFeatureJson(@NotNull PlacedFeature feature) {
        JsonObject main = new JsonObject();
        feature.feature().getKeyOrValue().ifLeft(
                key -> main.add("feature", new JsonPrimitive(key.getValue().toString()))
        ).ifRight(f -> main.add("feature", configuredFeatureJson(f)));
        JsonArray placements = new JsonArray();
        feature.placementModifiers().forEach(modifier -> placements.add(placementModifierJson(modifier)));
        main.add("placement", placements);
        return main;
    }

    public static @NotNull JsonObject configuredCarverJson(@NotNull ConfiguredCarver<?> carver) {
        CarverJsonParser parser = CARVER_PARSERS.get(carver.config().getClass());
        JsonObject main = new JsonObject();
        main.add("type", new JsonPrimitive(String.valueOf(Registry.CARVER.getId(carver.carver()))));
        JsonObject conf;
        if (parser == null) {
            conf = unknownJson(carver.config());
        } else {
            parser.in(carver.config());
            conf = parser.toJson();
        }
        main.add("config", conf);
        return main;
    }

    public static @NotNull JsonObject spawnersJson(SpawnSettings settings) {
        JsonObject main = new JsonObject();
        for (SpawnGroup group : SpawnGroup.values()) {
            Pool<SpawnSettings.SpawnEntry> pool = settings.getSpawnEntries(group);
            if (pool == null) continue;
            JsonArray a = new JsonArray();
            pool.getEntries().forEach(entry -> {
                JsonObject o = new JsonObject();
                o.add("type", new JsonPrimitive(String.valueOf(Registry.ENTITY_TYPE.getId(entry.type))));
                o.add("weight", new JsonPrimitive(entry.getWeight().getValue()));
                o.add("minCount", new JsonPrimitive(entry.minGroupSize));
                o.add("maxCount", new JsonPrimitive(entry.maxGroupSize));
                a.add(o);
            });
            main.add(group.asString(), a);
        }
        return main;
    }

    public static @NotNull JsonObject spawnCostsJson(SpawnSettings settings) {
        JsonObject main = new JsonObject();
        //noinspection unchecked
        ((IArrayProvider<EntityType<?>>)settings).dumpster$getArray().forEach(key -> {
            JsonObject o = new JsonObject();
            SpawnSettings.SpawnDensity density = settings.getSpawnDensity(key);
            if (density == null) return;
            o.add("energy_budget", new JsonPrimitive(density.getGravityLimit()));
            o.add("charge", new JsonPrimitive(density.getMass()));
            main.add(String.valueOf(Registry.ENTITY_TYPE.getId(key)), o);
        });
        return main;
    }

    public static @NotNull JsonObject biomeJson(@NotNull Biome biome) {
        JsonObject main = new JsonObject();
        main.add("precipitation", new JsonPrimitive(biome.getPrecipitation().getName()));
        main.add(C_TEMP, new JsonPrimitive(biome.getTemperature()));
        main.add("temperature_modifier", new JsonPrimitive(((BiomeWeatherAccessor)((Object)biome)).dumpster$getWeather().temperatureModifier().getName()));
        main.add("downfall", new JsonPrimitive(biome.getDownfall()));
        JsonObject effects = new JsonObject();
        effects.add("fog_color", new JsonPrimitive(biome.getFogColor()));
        effects.add("sky_color", new JsonPrimitive(biome.getSkyColor()));
        effects.add("water_color", new JsonPrimitive(biome.getWaterColor()));
        effects.add("water_fog_color", new JsonPrimitive(biome.getWaterFogColor()));
        effects.add("foliage_color", new JsonPrimitive(biome.getFoliageColor()));
        biome.getEffects().getGrassColor().ifPresent(c -> effects.add("grass_color", new JsonPrimitive(c)));
        effects.add("grass_color_modifier", new JsonPrimitive(biome.getEffects().getGrassColorModifier().getName()));
        biome.getParticleConfig().ifPresent(c -> {
            JsonObject part = new JsonObject();
            part.add("probability", new JsonPrimitive(((IFloatProvider) c).dumpster$getFloat()));
            part.add("options", objectJson(c.getParticle()));
            effects.add("particle", part);
        });
        biome.getLoopSound().ifPresent(s -> effects.add("ambient_sound", soundJson(s)));
        biome.getMoodSound().ifPresent(m -> {
            JsonObject mood = new JsonObject();
            mood.add(C_SOUND, soundJson(m.getSound()));
            mood.add("tick_delay", new JsonPrimitive(m.getCultivationTicks()));
            mood.add("block_search_extent", new JsonPrimitive(m.getSpawnRange()));
            mood.add("offset", new JsonPrimitive(m.getExtraDistance()));
            effects.add("mood_sound", mood);
        });
        biome.getAdditionsSound().ifPresent(a -> {
            JsonObject add = new JsonObject();
            add.add(C_SOUND, soundJson(a.getSound()));
            add.add("tick_chance", new JsonPrimitive(a.getChance()));
            effects.add("additions_sound", add);
        });
        biome.getMusic().ifPresent(m -> {
            JsonObject music = new JsonObject();
            music.add(C_SOUND, soundJson(m.getSound()));
            music.add("min_delay", new JsonPrimitive(m.getMinDelay()));
            music.add("max_delay", new JsonPrimitive(m.getMaxDelay()));
            music.add("replace_current_music", new JsonPrimitive(m.shouldReplaceCurrentMusic()));
            effects.add("music", music);
        });
        main.add("effects", effects);
        JsonObject carvers = new JsonObject();
        JsonArray airCarvers = new JsonArray();
        JsonArray liquidCarvers = new JsonArray();
        biome.getGenerationSettings().getCarversForStep(GenerationStep.Carver.AIR)
                .forEach(c -> airCarvers.add(jsonConfiguredCarverRegEntry(c)));
        biome.getGenerationSettings().getCarversForStep(GenerationStep.Carver.LIQUID)
                .forEach(c -> liquidCarvers.add(jsonConfiguredCarverRegEntry(c)));
        carvers.add("air", airCarvers);
        carvers.add("liquid", liquidCarvers);
        main.add("carvers", carvers);
        JsonArray features = new JsonArray();
        biome.getGenerationSettings().getFeatures().forEach(step -> {
            JsonArray c = new JsonArray();
            step.forEach(f -> c.add(jsonPlacedFeatureRegEntry(f)));
            features.add(c);
        });
        main.add("features", features);
        main.add("creature_spawn_probability", new JsonPrimitive(biome.getSpawnSettings().getCreatureSpawnProbability()));
        main.add("spawners", spawnersJson(biome.getSpawnSettings()));
        main.add("spawn_costs", spawnCostsJson(biome.getSpawnSettings()));
        return main;
    }

    public static @NotNull JsonElement parameterRangeJson(@NotNull MultiNoiseUtil.ParameterRange range) {
        if (range.min() == range.max()) return new JsonPrimitive(range.min());
        JsonObject main = new JsonObject();
        main.add("min", new JsonPrimitive(range.min()));
        main.add("max", new JsonPrimitive(range.max()));
        return main;
    }

    public static @NotNull JsonObject noiseHyperCubeJson(@NotNull MultiNoiseUtil.NoiseHypercube noise) {
        JsonObject main = new JsonObject();
        main.add(C_TEMP, parameterRangeJson(noise.temperature()));
        main.add("humidity", parameterRangeJson(noise.humidity()));
        main.add("continentalness", parameterRangeJson(noise.continentalness()));
        main.add("erosion", parameterRangeJson(noise.erosion()));
        main.add("weirdness", parameterRangeJson(noise.weirdness()));
        main.add("depth", parameterRangeJson(noise.depth()));
        main.add("offset", new JsonPrimitive(noise.offset()));
        return main;
    }

    public static JsonObject densityFunctionJson(DensityFunction function) {
        Optional<JsonElement> e = DensityFunction.CODEC.encodeStart(JsonOps.INSTANCE, function).result();
        if (e.isEmpty() || !e.get().isJsonObject()) return unknownJson(function);
        return e.get().getAsJsonObject();
    }

    public static JsonObject materialRuleJson(MaterialRules.MaterialRule rule) {
        Optional<JsonElement> e = MaterialRules.MaterialRule.CODEC.encodeStart(JsonOps.INSTANCE, rule).result();
        if (e.isEmpty() || !e.get().isJsonObject()) return unknownJson(rule);
        return e.get().getAsJsonObject();
    }

    public static JsonObject biomeSourceJson(BiomeSource source) {
        Optional<JsonElement> e = BiomeSource.CODEC.encodeStart(JsonOps.INSTANCE, source).result();
        if (e.isEmpty() || ! e.get().isJsonObject()) return unknownJson(source);
        return e.get().getAsJsonObject();
    }

    public static JsonObject placementModifierJson(PlacementModifier modifier) {
        Optional<JsonElement> e = PlacementModifier.CODEC.encodeStart(JsonOps.INSTANCE, modifier).result();
        if (e.isEmpty() || !e.get().isJsonObject()) return unknownJson(modifier);
        return e.get().getAsJsonObject();
    }

    public static JsonObject structurePoolJson(StructurePool pool) {
        Optional<JsonElement> e = StructurePool.CODEC.encodeStart(JsonOps.INSTANCE, pool).result();
        if (e.isEmpty() || !e.get().isJsonObject()) return unknownJson(pool);
        return e.get().getAsJsonObject();
    }

    public static @NotNull JsonObject flatLevelGeneratorPresetJson(FlatLevelGeneratorPreset preset) {
        Optional<JsonElement> e = FlatLevelGeneratorPreset.CODEC.encodeStart(JsonOps.INSTANCE, preset).result();
        if (e.isEmpty() || !e.get().isJsonObject()) return unknownJson(preset);
        return e.get().getAsJsonObject();
    }

    public static @NotNull JsonObject noiseJson(DoublePerlinNoiseSampler.NoiseParameters noise) {
        Optional<JsonElement> e = DoublePerlinNoiseSampler.NoiseParameters.CODEC.encodeStart(JsonOps.INSTANCE, noise).result();
        if (e.isEmpty() || !e.get().isJsonObject()) return unknownJson(noise);
        return e.get().getAsJsonObject();
    }

    public static @NotNull JsonObject processorListJson(@NotNull StructureProcessorList processorList) {
        JsonArray array = new JsonArray();
        processorList.getList().forEach(e -> array.add(objectJson(e)));
        JsonObject main = new JsonObject();
        main.add("processors", array);
        return main;
    }

    public static @NotNull JsonObject noiseRouterJson(@NotNull NoiseRouter router) {
        JsonObject main = new JsonObject();
        main.add("initial_density_without_jaggedness", jsonDensityFunctionToReg(router.initialDensityWithoutJaggedness()));
        main.add("final_density", jsonDensityFunctionToReg(router.finalDensity()));
        main.add("barrier", jsonDensityFunctionToReg(router.barrierNoise()));
        main.add("fluid_level_floodedness", jsonDensityFunctionToReg(router.fluidLevelFloodednessNoise()));
        main.add("fluid_level_spread", jsonDensityFunctionToReg(router.fluidLevelSpreadNoise()));
        main.add("lava", jsonDensityFunctionToReg(router.lavaNoise()));
        main.add("vein_toggle", jsonDensityFunctionToReg(router.veinToggle()));
        main.add("vein_ridged", jsonDensityFunctionToReg(router.veinRidged()));
        main.add("vein_gap", jsonDensityFunctionToReg(router.veinGap()));
        main.add(C_TEMP, jsonDensityFunctionToReg(router.temperature()));
        main.add("vegetation", jsonDensityFunctionToReg(router.vegetation()));
        main.add("continents", jsonDensityFunctionToReg(router.continents()));
        main.add("erosion", jsonDensityFunctionToReg(router.erosion()));
        main.add("depth", jsonDensityFunctionToReg(router.depth()));
        main.add("ridges", jsonDensityFunctionToReg(router.ridges()));
        return main;
    }

    public static @NotNull JsonObject chunkGeneratorSettingsJson(@NotNull ChunkGeneratorSettings settings) {
        JsonObject main = new JsonObject();
        main.add("sea_level", new JsonPrimitive(settings.seaLevel()));
        main.add("disable_mob_generation", new JsonPrimitive(settings.mobGenerationDisabled()));
        main.add("ore_veins_enabled", new JsonPrimitive(settings.oreVeins()));
        main.add("aquifers_enabled", new JsonPrimitive(settings.aquifers()));
        main.add("legacy_random_source", new JsonPrimitive(settings.usesLegacyRandom()));
        main.add("default_block", blockStateJSon(settings.defaultBlock()));
        main.add("default_fluid", fluidStateJson(settings.defaultFluid().getFluidState()));
        JsonArray spawnTargets = new JsonArray();
        settings.spawnTarget().forEach(target -> spawnTargets.add(noiseHyperCubeJson(target)));
        main.add("spawn_target", spawnTargets);
        JsonObject noise = new JsonObject();
        noise.add("min_y", new JsonPrimitive(settings.generationShapeConfig().minimumY()));
        noise.add("height", new JsonPrimitive(settings.generationShapeConfig().height()));
        noise.add("size_horizontal", new JsonPrimitive(settings.generationShapeConfig().horizontalSize()));
        noise.add("size_vertical", new JsonPrimitive(settings.generationShapeConfig().verticalSize()));
        main.add("noise", noise);
        main.add("noise_router", noiseRouterJson(settings.noiseRouter()));
        main.add("surface_rule", materialRuleJson(settings.surfaceRule()));
        return main;
    }

    public static JsonElement jsonPlacedFeatureRegEntry(@NotNull RegistryEntry<PlacedFeature> entry) {
        return entry.getKeyOrValue()
                .mapLeft(key -> (JsonElement)(new JsonPrimitive(key.getValue().toString())))
                .mapRight(JsonUtils::placedFeatureJson).orThrow();
    }

    public static JsonElement jsonStructureProcessorListRegEntry(@NotNull RegistryEntry<StructureProcessorList> entry) {
        return entry.getKeyOrValue()
                .mapLeft(k -> (JsonElement)(new JsonPrimitive(k.toString())))
                .mapRight(l -> {
                    JsonArray array = new JsonArray();
                    l.getList().forEach(p -> array.add(JsonUtils.objectJson(p)));
                    return array;
                }).orThrow();
    }

    public static JsonElement jsonConfiguredCarverRegEntry(@NotNull RegistryEntry<ConfiguredCarver<?>> entry) {
        return entry.getKeyOrValue()
                .mapLeft(key -> (JsonElement)(new JsonPrimitive(key.getValue().toString())))
                .mapRight(JsonUtils::configuredCarverJson).orThrow();
    }

    public static JsonElement jsonBlockRegEntry(@NotNull RegistryEntry<Block> entry) {
        return entry.getKeyOrValue()
                .mapLeft(key -> (JsonElement)(new JsonPrimitive(String.valueOf(key.getValue()))))
                .mapRight(b -> new JsonPrimitive(String.valueOf(Registry.BLOCK.getId(b))))
                .orThrow();
    }

    public static JsonElement jsonDimensionTypeRegEntry(@NotNull RegistryEntry<DimensionType> entry) {
        return entry.getKeyOrValue()
                .mapLeft(key -> (JsonElement)(new JsonPrimitive(String.valueOf(key.getValue()))))
                .mapRight(JsonUtils::dimensionTypeJson).orThrow();
    }

    public static JsonElement jsonStructureRegEntry(@NotNull RegistryEntry<Structure> entry) {
        return entry.getKeyOrValue()
                .mapLeft(key -> (JsonElement)(new JsonPrimitive(String.valueOf(key.getValue()))))
                .mapRight(JsonUtils::structureJson).orThrow();
    }

    public static JsonElement jsonStructureSetRegEntry(@NotNull RegistryEntry<StructureSet> entry) {
        return entry.getKeyOrValue()
                .mapLeft(key -> (JsonElement)(new JsonPrimitive(String.valueOf(key.getValue()))))
                .mapRight(JsonUtils::structureSetJson).orThrow();
    }

    public static JsonElement jsonBiomeRegEntry(@NotNull RegistryEntry<Biome> entry) {
        return entry.getKeyOrValue()
                .mapLeft(key -> (JsonElement)(new JsonPrimitive(String.valueOf(key.getValue()))))
                .mapRight(JsonUtils::biomeJson).orThrow();
    }

    public static JsonElement jsonDensityFunctionToReg(DensityFunction function) {
        Optional<RegistryKey<DensityFunction>> key = BuiltinRegistries.DENSITY_FUNCTION.getKey(function);
        if (key.isPresent()) return new JsonPrimitive(String.valueOf(key.get().getValue()));
        return densityFunctionJson(function);
    }

    public static JsonElement jsonChunkGeneratorSettingsRegEntry(RegistryEntry<ChunkGeneratorSettings> entry) {
        return entry.getKeyOrValue()
                .mapLeft(key -> (JsonElement)(new JsonPrimitive(String.valueOf(key.getValue()))))
                .mapRight(JsonUtils::chunkGeneratorSettingsJson).orThrow();
    }
}
