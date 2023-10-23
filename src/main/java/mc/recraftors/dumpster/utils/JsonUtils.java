package mc.recraftors.dumpster.utils;

import com.google.gson.*;
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
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class JsonUtils {
    private JsonUtils() {}
    public static final String C_SOUND = "sound";

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

    static FeatureJsonParser resolveFeatureParser(ConfiguredFeature<?,?> feature, Identifier id, FeatureJsonParser parser) {
        if (!parser.getClass().isAnnotationPresent(TargetFeatureConfigType.class)) return null;
        TargetFeatureConfigType type = parser.getClass().getAnnotation(TargetFeatureConfigType.class);
        for (String s : type.supports()) {
            if (id.equals(Identifier.tryParse(s)) && parser.in(feature.config())) {
                return parser;
            }
        }
        return null;
    }

    static FeatureJsonParser getFeatureParser(ConfiguredFeature<?,?> feature) {
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
    public static JsonElement nbtJson(NbtElement nbt) {
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

    public static JsonArray vec3iJson(Vec3i vec) {
        JsonArray a = new JsonArray();
        a.add(vec.getX());
        a.add(vec.getY());
        a.add(vec.getZ());
        return a;
    }

    public static JsonArray blockPosJson(BlockPos pos) {
        return vec3iJson(new Vec3i(pos.getX(), pos.getY(), pos.getZ()));
    }

    public static JsonObject noiseJson(DoublePerlinNoiseSampler.NoiseParameters noise) {
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

    public static JsonObject unknownJson(Object o) {
        JsonObject obj = new JsonObject();
        obj.add("type", new JsonPrimitive("unknown"));
        obj.add("class", new JsonPrimitive(o.getClass().getName()));
        return obj;
    }

    public static JsonObject objectJson(Object o) {
        try {
            return ((Objectable)o).toJson();
        } catch (ClassCastException e) {
            return unknownJson(o);
        }
    }

    static void addProperties(State<?,?> state, JsonObject object) {
        Collection<Property<?>> properties = state.getProperties();
        if (!properties.isEmpty()) {
            JsonObject props = new JsonObject();
            properties.forEach(p -> props.add(p.getName(), new JsonPrimitive(state.get(p).toString())));
            object.add("Properties", props);
        }
    }

    public static JsonObject blockStateJSon(BlockState state) {
        JsonObject main = new JsonObject();
        main.add("Name", new JsonPrimitive(String.valueOf(Registry.BLOCK.getId(state.getBlock()))));
        addProperties(state, main);
        return main;
    }

    public static JsonObject fluidStateJson(FluidState state) {
        JsonObject main = new JsonObject();
        main.add("Name", new JsonPrimitive(String.valueOf(Registry.FLUID.getId(state.getFluid()))));
        addProperties(state, main);
        return main;
    }

    public static JsonElement jsonBlockRegEntry(RegistryEntry<Block> entry) {
        return entry.getKeyOrValue()
                .mapLeft(key -> (new JsonPrimitive(String.valueOf(key.getValue()))))
                .mapRight(b -> new JsonPrimitive(String.valueOf(Registry.BLOCK.getId(b))))
                .orThrow();
    }

    public static JsonObject advancementToJson(Advancement adv) {
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

    public static JsonObject dimensionJson(DimensionType dim) {
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

    public static JsonObject soundJson(SoundEvent e) {
        JsonObject main = new JsonObject();
        main.add("type", new JsonPrimitive(e.getId().toString()));
        if (((IBooleanProvider)e).dumpster$getBool()) {
            main.add("range", new JsonPrimitive(((IFloatProvider)e).dumpster$getFloat()));
        }
        return main;
    }

    public static JsonElement jsonConfiguredFeature(ConfiguredFeature<?,?> feature) {
        FeatureJsonParser parser = getFeatureParser(feature);
        return parser == null ? unknownJson(feature) : parser.toJson();
    }

    public static JsonElement jsonPlacedFeature(PlacedFeature feature) {
        JsonObject o = new JsonObject();
        feature.feature().getKeyOrValue().ifLeft(
                key -> o.add("feature", new JsonPrimitive(key.getValue().toString()))
        ).ifRight(f -> o.add("feature", jsonConfiguredFeature(f)));
        //TODO: placement
        return o;
    }

    public static JsonElement jsonPlacedFeatureRegEntry(RegistryEntry<PlacedFeature> entry) {
        return entry.getKeyOrValue()
                .mapLeft(key -> (JsonElement)(new JsonPrimitive(key.getValue().toString())))
                .mapRight(JsonUtils::jsonPlacedFeature).orThrow();
    }

    public static JsonElement jsonStructureProcessorListRegEntry(RegistryEntry<StructureProcessorList> entry) {
        return entry.getKeyOrValue()
                .mapLeft(k -> (JsonElement)(new JsonPrimitive(k.toString())))
                .mapRight(l -> {
                    JsonArray array = new JsonArray();
                    l.getList().forEach(p -> array.add(JsonUtils.objectJson(p)));
                    return array;
                }).orThrow();
    }

    public static JsonObject spawnersJson(SpawnSettings settings) {
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
        main.add("temperature", new JsonPrimitive(biome.getTemperature()));
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
            part.add("options", ((Objectable)c.getParticle()).toJson());
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
        biome.getGenerationSettings().getCarversForStep(GenerationStep.Carver.AIR).forEach(c -> {
            Optional<RegistryKey<ConfiguredCarver<?>>> k = c.getKey();
            if (k.isPresent()) {
                airCarvers.add(k.get().getValue().toString());
                return;
            }
            JsonObject car = new JsonObject();
            car.add("type", new JsonPrimitive(k.get().getValue().toString()));
            car.add("config", ((Objectable)((Object)c.value())).toJson());
            airCarvers.add(car);
        });
        carvers.add("air", airCarvers);
        JsonArray liquidCarvers = new JsonArray();
        biome.getGenerationSettings().getCarversForStep(GenerationStep.Carver.LIQUID).forEach(c -> {
            Optional<RegistryKey<ConfiguredCarver<?>>> k = c.getKey();
            if (k.isPresent()) {
                liquidCarvers.add(k.get().getValue().toString());
                return;
            }
            JsonObject car = new JsonObject();
            car.add("type", new JsonPrimitive(Registry.CARVER.getId(c.value().carver()).toString()));
            CarverJsonParser parser = CARVER_PARSERS.get(c.value().config().getClass());
            JsonObject conf;
            if (parser == null || !parser.in(c.value().config())) {
                conf = unknownJson(c.value().config());
            } else {
                conf = parser.toJson();
            }
            car.add("config", conf);
            liquidCarvers.add(car);
        });
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
}
