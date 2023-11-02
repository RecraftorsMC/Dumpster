package mc.recraftors.dumpster.utils;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.accessors.IObjectProvider;
import mc.recraftors.dumpster.utils.accessors.IStringable;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.structure.Structure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class Utils {
    public static final String MOD_ID = "dumpster";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final String ERROR_TOAST_TITLE = "dumpster.toast.error.title";
    public static final String ERROR_TOAST_DESC = "dumpster.toast.error.desc";

    public static final Lock lock = new ReentrantLock();
    private static final Collection<Registry<?>> REGISTRIES = new LinkedHashSet<>();

    public static void reg(Registry<?> reg) {
        if (reg == null) {
            return;
        }
        REGISTRIES.add(reg);
    }

    private static int dumpRegistries(@NotNull LocalDateTime now) {
        AtomicInteger i = new AtomicInteger();
        Set<Identifier> err = new HashSet<>();
        for (Registry<?> reg : REGISTRIES) {
            Collection<JsonObject> col = JsonUtils.registryJson(reg);
            Identifier type = reg.getKey().getValue();
            if (FileUtils.storeRegistry(col, type, now, i)) {
                err.add(type);
                i.incrementAndGet();
            }
        }
        if (i.get() > 0) {
            FileUtils.writeErrors(Map.of("Registries", err));
        }
        return i.get();
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpTags(
            @NotNull World world, @NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        for (Registry<?> reg : REGISTRIES) {
            try {
                world.getRegistryManager().get(reg.getKey()).streamTagsAndEntries().forEach(pair -> {
                    Collection<RegistryEntry> entries = new ArrayList<>();
                    Identifier id = pair.getFirst().id();
                    pair.getSecond().forEach(entry -> {
                        if (entry.getKey().isEmpty()) return;
                        Identifier v = entry.getKey().get().getValue();
                        Class<?> tClass = entry.value().getClass();
                        String s = entry.getKey().get().toString();
                        entries.add(new RegistryEntry(v, tClass, s));
                    });
                    FileUtils.storeTag(entries, reg.getKey().getValue(), id, now, i);
                });
            } catch (IllegalStateException e) {
                i.incrementAndGet();
                err.add(reg.getKey().getValue());
            }
        }
        return Map.of("Tags", err);
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpRecipes(
            @NotNull World world, @NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> nonParsableTypes = new HashSet<>();
        Set<Identifier> erroredRecipes = new HashSet<>();
        world.getRecipeManager().values().forEach(recipe -> {
            InResult.Result<JsonObject> result = JsonUtils.recipeJson(recipe);
            if (result.result() == InResult.FAILURE) erroredRecipes.add(recipe.getId());
            if (result.result() == InResult.IGNORED) nonParsableTypes.add(result.type());
            if (result.result() != null) {
                FileUtils.storeRecipe(result.value(), result.id(), result.type(), now, result.isSpecial(), i);
            }
        });
        nonParsableTypes.forEach(e -> LOGGER.error("Unable to parse recipes of type {}", e));
        i.addAndGet(erroredRecipes.size() + nonParsableTypes.size());
        Map<String, Set<Identifier>> out = new HashMap<>();
        if (!nonParsableTypes.isEmpty()) out.put("Recipe Types", nonParsableTypes);
        if (!erroredRecipes.isEmpty()) out.put("Recipes", erroredRecipes);
        return Map.copyOf(out);
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpLootTables(
            @NotNull ServerWorld world, @NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> errTables = new HashSet<>();
        LootManager manager = world.getServer().getLootManager();
        manager.getTableIds().forEach(id -> {
            LootTable table = manager.getTable(id);
            try {
                JsonObject o = LootManager.toJson(table).getAsJsonObject();
                JsonUtils.jsonClearNull(o);
                FileUtils.storeLootTable(o, id, now, i);
            } catch (JsonIOException|NullPointerException|IllegalStateException e) {
                i.incrementAndGet();
                errTables.add(id);
            }
        });
        if (errTables.isEmpty()) return Map.of();
        return Map.of("Loot Tables", errTables);
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpAdvancements(
            @NotNull ServerWorld world, @NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        world.getServer().getAdvancementLoader().getAdvancements().forEach(adv -> {
            JsonObject o = JsonUtils.advancementJson(adv);
            if (FileUtils.storeAdvancement(o, adv.getId(), now, i)) {
                err.add(adv.getId());
            }
        });
        if (!err.isEmpty()) return Map.of("Advancements", err);
        return Map.of();
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpDimensions(
            @NotNull ServerWorld world, @NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        @SuppressWarnings("unchecked")
        GeneratorOptions options = ((IObjectProvider<GeneratorOptions>) world.getStructureAccessor()).dumpster$getObject();
        options.getDimensions().getEntrySet().forEach(entry -> {
            Identifier id = entry.getKey().getValue();
            if (FileUtils.storeDimension(JsonUtils.dimensionJson(entry.getValue()), id, now, i)) {
                err.add(entry.getKey().getValue());
            }
        });
        if (!err.isEmpty()) return Map.of("Dimensions", err);
        return Map.of();
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpDimensionTypes(
            @NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        BuiltinRegistries.DIMENSION_TYPE.getEntrySet().forEach(e -> {
            if (FileUtils.storeDimensionType(JsonUtils.dimensionTypeJson(e.getValue()), e.getKey().getValue(), now, i)) {
                err.add(e.getKey().getValue());
            }
        });
        if (!err.isEmpty()) return Map.of("Dimension Types", err);
        return Map.of();
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpFunctions(
            @NotNull ServerWorld world, @NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        world.getServer().getCommandFunctionManager().getAllFunctions().forEach(id -> {
            Optional<CommandFunction> oF = world.getServer().getCommandFunctionManager().getFunction(id);
            if (oF.isEmpty()) return;
            CommandFunction function = oF.get();
            String s = String.join("\n", List.of(Arrays.stream(function.getElements()).map(IStringable.class::cast).map(IStringable::dumpster$stringify).toArray(String[]::new)));
            if (FileUtils.storeFunction(s, function.getId(), now, i)) {
                err.add(function.getId());
            }
        });
        if (!err.isEmpty()) return Map.of("Functions", err);
        return Map.of();
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpStructureTemplates(
            @NotNull ServerWorld world, @NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        Iterator<Identifier> iter = world.getStructureTemplateManager().streamTemplates().iterator();
        Identifier id = new Identifier("a");
        while (id != null) {
            try {
                if (!iter.hasNext()) break;
                id = iter.next();
                Optional<StructureTemplate> opt = world.getStructureTemplateManager().getTemplate(id);
                if (opt.isEmpty()) {
                    err.add(id);
                    i.incrementAndGet();
                    continue;
                }
                StructureTemplate template = opt.get();
                NbtCompound nbt = template.writeNbt(new NbtCompound());
                if (FileUtils.storeStructureTemplate(nbt, id, now, i)) {
                    err.add(id);
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        if (!err.isEmpty()) return Map.of("Structure Templates", err);
        return Map.of();
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpBiomes(@NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        BuiltinRegistries.DYNAMIC_REGISTRY_MANAGER.getManaged(Registry.BIOME_KEY).getEntrySet().forEach(e -> {
            Identifier id = e.getKey().getValue();
            Biome b = e.getValue();
            if (b == null) {
                err.add(id);
                i.getAndIncrement();
                return;
            }
            JsonObject o = JsonUtils.biomeJson(b);
            if (FileUtils.storeWorldgen(o, id, "biome", now, i)) {
                err.add(id);
            }
        });
        if (!err.isEmpty()) {
            return Map.of("Biomes", err);
        }
        return Map.of();
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpCarvers(@NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        BuiltinRegistries.DYNAMIC_REGISTRY_MANAGER.getManaged(Registry.CONFIGURED_CARVER_KEY).getEntrySet().forEach(e -> {
            Identifier id = e.getKey().getValue();
            ConfiguredCarver<?> carver = e.getValue();
            if (carver == null) {
                err.add(id);
                i.incrementAndGet();
                return;
            }
            JsonObject o = JsonUtils.configuredCarverJson(carver);
            if (FileUtils.storeWorldgen(o, id, "configured_carver", now, i)) {
                err.add(id);
            }
        });
        if (!err.isEmpty()) return Map.of("Configured Carvers", err);
        return Map.of();
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpConfiguredFeatures(
            @NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        BuiltinRegistries.CONFIGURED_FEATURE.getEntrySet().forEach(e -> {
            Identifier id = e.getKey().getValue();
            ConfiguredFeature<?,?> feature = e.getValue();
            if (feature == null) {
                err.add(id);
                i.incrementAndGet();
                return;
            }
            JsonObject o = JsonUtils.configuredFeatureJson(feature);
            if (FileUtils.storeWorldgen(o, id, "configured_feature", now, i)) {
                err.add(id);
            }
        });
        if (err.isEmpty()) return Map.of();
        return Map.of("Configured Features", err);
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpDensityFunctions(
            @NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        BuiltinRegistries.DENSITY_FUNCTION.getEntrySet().forEach(e -> {
            Identifier id = e.getKey().getValue();
            DensityFunction function = e.getValue();
            if (function == null) {
                err.add(id);
                i.incrementAndGet();
                return;
            }
            JsonObject o = JsonUtils.densityFunctionJson(function);
            if (FileUtils.storeWorldgen(o, id, "density_function", now, i)) {
                err.add(id);
            }
        });
        if (err.isEmpty()) return Map.of();
        return Map.of("Density Functions", err);
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpFlatLevelGeneratorPresets(
            @NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        BuiltinRegistries.FLAT_LEVEL_GENERATOR_PRESET.getEntrySet().forEach(e -> {
            Identifier id = e.getKey().getValue();
            FlatLevelGeneratorPreset preset = e.getValue();
            if (preset == null) {
                err.add(id);
                i.incrementAndGet();
                return;
            }
            JsonObject o = JsonUtils.flatLevelGeneratorPresetJson(preset);
            if (FileUtils.storeWorldgen(o, id, "flat_level_generator_preset", now, i)) {
                err.add(id);
            }
        });
        if (err.isEmpty()) return Map.of();
        return Map.of("Flat Level Generator Presets", err);
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpNoise(
            @NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        BuiltinRegistries.NOISE_PARAMETERS.getEntrySet().forEach(e -> {
            Identifier id= e.getKey().getValue();
            DoublePerlinNoiseSampler.NoiseParameters noise = e.getValue();
            if (noise == null) {
                err.add(id);
                i.incrementAndGet();
                return;
            }
            JsonObject o = JsonUtils.noiseJson(noise);
            if (FileUtils.storeWorldgen(o, id, "noise", now, i)) {
                err.add(id);
            }
        });
        if (err.isEmpty()) return Map.of();
        return Map.of("Noise", err);
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpNoiseSettings(
            @NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.getEntrySet().forEach(e -> {
            Identifier id = e.getKey().getValue();
            ChunkGeneratorSettings settings = e.getValue();
            if (settings == null) {
                err.add(id);
                i.incrementAndGet();
                return;
            }
            JsonObject o = JsonUtils.chunkGeneratorSettingsJson(settings);
            if (FileUtils.storeWorldgen(o, id, "noise_settings", now, i)) {
                err.add(id);
            }
        });
        if (err.isEmpty()) return Map.of();
        return Map.of("Noise Settings", err);
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpPlacedFeatures(
            @NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        BuiltinRegistries.PLACED_FEATURE.getEntrySet().forEach(e -> {
            Identifier id = e.getKey().getValue();
            PlacedFeature feature = e.getValue();
            if (feature == null) {
                err.add(id);
                i.incrementAndGet();
                return;
            }
            JsonObject o = JsonUtils.placedFeatureJson(feature);
            if (FileUtils.storeWorldgen(o, id, "placed_feature", now, i)) {
                err.add(id);
            }
        });
        if (err.isEmpty()) return Map.of();
        return Map.of("Placed Features", err);
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpProcessorLists(
            @NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        BuiltinRegistries.STRUCTURE_PROCESSOR_LIST.getEntrySet().forEach(e -> {
            Identifier id = e.getKey().getValue();
            StructureProcessorList list = e.getValue();
            if (list == null) {
                err.add(id);
                i.incrementAndGet();
                return;
            }
            JsonObject o = JsonUtils.processorListJson(list);
            if (FileUtils.storeWorldgen(o, id, "processor_list", now, i)) {
                err.add(id);
            }
        });
        if (err.isEmpty()) return Map.of();
        return Map.of("Processor Lists", err);
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpStructures(
            @NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        BuiltinRegistries.STRUCTURE.getEntrySet().forEach(e -> {
            Identifier id = e.getKey().getValue();
            Structure structure = e.getValue();
            if (structure == null) {
                err.add(id);
                i.incrementAndGet();
                return;
            }
            JsonObject o = JsonUtils.structureJson(structure);
            if (FileUtils.storeWorldgen(o, id, "structure", now, i)) {
                err.add(id);
            }
        });
        if (err.isEmpty()) return Map.of();
        return Map.of("Structures", err);
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpStructureSets(
            @NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        BuiltinRegistries.STRUCTURE_SET.getEntrySet().forEach(e -> {
            Identifier id = e.getKey().getValue();
            StructureSet set = e.getValue();
            if (set == null) {
                err.add(id);
                i.incrementAndGet();
                return;
            }
            JsonObject o = JsonUtils.structureSetJson(set);
            if (FileUtils.storeWorldgen(o, id, "structure_set", now, i)) {
                err.add(id);
            }
        });
        if (err.isEmpty()) return Map.of();
        return Map.of("Structure Sets", err);
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpTemplatePools(
            @NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        BuiltinRegistries.STRUCTURE_POOL.getEntrySet().forEach(e -> {
            Identifier id = e.getKey().getValue();
            StructurePool pool = e.getValue();
            if (pool == null) {
                err.add(id);
                i.incrementAndGet();
                return;
            }
            JsonObject o = JsonUtils.structurePoolJson(pool);
            if (FileUtils.storeWorldgen(o, id, "template_pool", now, i)) {
                err.add(id);
            }
        });
        if (err.isEmpty()) return Map.of();
        return Map.of("Template Pools", err);
    }

    private static @NotNull @Unmodifiable Map<String, Set<Identifier>> dumpWorldPresets(
            @NotNull LocalDateTime now, @NotNull AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        BuiltinRegistries.WORLD_PRESET.getEntrySet().forEach(e -> {
            Identifier id = e.getKey().getValue();
            WorldPreset preset = e.getValue();
            if (preset == null) {
                err.add(id);
                i.incrementAndGet();
                return;
            }
            JsonObject o = JsonUtils.worldPresetJson(preset);
            if (FileUtils.storeWorldgen(o, id, "world_preset", now, i)) {
                err.add(id);
            }
        });
        if (err.isEmpty()) return Map.of();
        return Map.of("World Presets", err);
    }

    private static @NotNull Map<String, Set<Identifier>> dumpWorldgen(
            @NotNull LocalDateTime now, @NotNull AtomicInteger i, @NotNull DumpCall.Worldgen call) {
        Map<String, Set<Identifier>> errMap = new LinkedHashMap<>();
        if (call.biomes() && ConfigUtils.doDumpWorldgenBiomes()) {
            errMap.putAll(dumpBiomes(now, i));
        }
        if (call.carvers() && ConfigUtils.doDumpWorldgenCarvers()) {
            errMap.putAll(dumpCarvers(now, i));
        }
        if (call.features() && ConfigUtils.doDumpWorldgenConfiguredFeatures()) {
            errMap.putAll(dumpConfiguredFeatures(now, i));
        }
        if (call.densityFunctions() && ConfigUtils.doDumpWorldgenDensityFunctions()) {
            errMap.putAll(dumpDensityFunctions(now, i));
        }
        if (call.flatGeneratorPresets() && ConfigUtils.doDumpWorldgenFlatGeneratorPresets()) {
            errMap.putAll(dumpFlatLevelGeneratorPresets(now, i));
        }
        if (call.noise() && ConfigUtils.doDumpWorldgenNoise()) {
            errMap.putAll(dumpNoise(now, i));
        }
        if (call.noiseSettings() && ConfigUtils.doDumpWorldgenNoiseSettings()) {
            errMap.putAll(dumpNoiseSettings(now, i));
        }
        if (call.placedFeature() && ConfigUtils.doDumpWorldgenPlacedFeatures()) {
            errMap.putAll(dumpPlacedFeatures(now, i));
        }
        if (call.processorList() && ConfigUtils.doDumpWorldgenProcessorLists()) {
            errMap.putAll(dumpProcessorLists(now, i));
        }
        if (call.structure() && ConfigUtils.doDumpWorldgenStrucures()) {
            errMap.putAll(dumpStructures(now, i));
        }
        if (call.structureSet() && ConfigUtils.doDumpWorldgenStrucureSets()) {
            errMap.putAll(dumpStructureSets(now, i));
        }
        if (call.templatePool() && ConfigUtils.doDumpWorldgenTemplatePools()) {
            errMap.putAll(dumpTemplatePools(now, i));
        }
        if (call.worldPreset() && ConfigUtils.doDumpWorldgenWorldPresets()) {
            errMap.putAll(dumpWorldPresets(now, i));
        }
        return errMap;
    }

    public static void  dumpDataServer(@NotNull ServerWorld w, Map<String, @NotNull Set<Identifier>> errMap,
                                       @NotNull LocalDateTime now, @NotNull AtomicInteger i, @NotNull DumpCall.Data call) {
        if (call.lootTables() && ConfigUtils.doDumpLootTables()) {
            errMap.putAll(dumpLootTables(w, now, i));
        }
        if (call.advancements() && ConfigUtils.doDumpAdvancements()) {
            errMap.putAll(dumpAdvancements(w, now, i));
        }
        if (call.functions() && ConfigUtils.doDumpFunctions()) {
            errMap.putAll(dumpFunctions(w, now, i));
        }
        if (call.structures() && ConfigUtils.doDumpStructureTemplates()) {
            errMap.putAll(dumpStructureTemplates(w, now, i));
        }
        if (call.dimensions() && ConfigUtils.doDumpDimensions()) {
            errMap.putAll(dumpDimensions(w, now, i));
        }
    }

    public static int dumpData(@NotNull World world, @NotNull LocalDateTime now, @NotNull DumpCall.Data call) {
        AtomicInteger i = new AtomicInteger();
        Map<String, Set<Identifier>> errMap = new LinkedHashMap<>();
        if (call.tags() && ConfigUtils.doDataDumpTags()) {
            errMap.putAll(dumpTags(world, now, i));
        }
        if (call.recipes() && ConfigUtils.doDataDumpRecipes()) {
            errMap.putAll(dumpRecipes(world, now, i));
        }
        if (call.dimensionTypes() && ConfigUtils.doDumpDimensionTypes()) {
            errMap.putAll(dumpDimensionTypes(now, i));
        }
        if (world instanceof ServerWorld w) {
            dumpDataServer(w, errMap, now, i, call);
        }
        if (call.worldgen() && call.worldgenO() != null) {
            errMap.putAll(dumpWorldgen(now, i, call.worldgenO()));
        }
        if (i.get() > 0) {
            FileUtils.writeErrors(errMap);
        }
        return i.get();
    }

    public static int dump(@Nullable World w, @NotNull DumpCall call) {
        lock.lock();
        FileUtils.clearIfNeeded();
        LocalDateTime now = LocalDateTime.now();
        int n = 0;
        if (call.registries()) {
            n = dumpRegistries(now);
        }
        if (call.data() && w != null && call.dataO() != null) {
            n += dumpData(w, now, call.dataO());
        }
        lock.unlock();
        return n;
    }

    public static void debug() {
        if (!ConfigUtils.isDebugEnabled()) return;
        JsonUtils.debug(REGISTRIES);
    }

    public static String normalizeId(Identifier id) {
        return id.getNamespace() + File.separator + normalizeIdPath(id);
    }

    public static String normalizeIdPath(Identifier id) {
        return String.join(File.separator, id.getPath().split("/"));
    }

    private Utils() {}
}
