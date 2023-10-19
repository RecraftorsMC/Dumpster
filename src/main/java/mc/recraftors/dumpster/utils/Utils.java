package mc.recraftors.dumpster.utils;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import mc.recraftors.dumpster.recipes.RecipeJsonParser;
import mc.recraftors.dumpster.recipes.TargetRecipeType;
import mc.recraftors.dumpster.utils.accessors.IStringable;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
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
    private static final Map<Identifier, RecipeJsonParser> RECIPE_PARSERS;

    static {
        Map<Identifier, RecipeJsonParser> recipeParserMap = new HashMap<>();
        FabricLoader.getInstance().getEntrypoints("recipe-dump", RecipeJsonParser.class).forEach(e -> {
            if (!e.getClass().isAnnotationPresent(TargetRecipeType.class)) return;
            TargetRecipeType type = e.getClass().getAnnotation(TargetRecipeType.class);
            Identifier id = Identifier.tryParse(type.value());
            if (id == null) return;
            if (recipeParserMap.containsKey(id)) {
                RecipeJsonParser o = recipeParserMap.get(id);
                if (!o.getClass().isAnnotationPresent(TargetRecipeType.class) ||
                        o.getClass().getAnnotation(TargetRecipeType.class).priority() < type.priority()){
                    recipeParserMap.put(id, e);
                }
            } else {
                recipeParserMap.put(id, e);
            }
        });
        RECIPE_PARSERS = recipeParserMap;
    }

    public static void reg(Registry<?> reg) {
        if (reg == null) {
            return;
        }
        REGISTRIES.add(reg);
    }

    public static int dumpRegistries(LocalDateTime now) {
        lock.lock();
        AtomicInteger i = new AtomicInteger();
        Set<Identifier> err = new HashSet<>();
        for (Registry<?> reg : REGISTRIES) {
            Collection<RegistryEntry> entries = new ArrayList<>();
            reg.getEntrySet().forEach(entry -> {
                Identifier id = entry.getKey().getValue();
                Class<?> tClass = entry.getValue().getClass();
                String s = entry.getKey().toString();
                RegistryEntry r = new RegistryEntry(id, tClass, s);
                entries.add(r);
            });
            try {
                String folder = ConfigUtils.dumpFileMainFolder();
                if (ConfigUtils.doDumpFileOrganizeFolderByDate()) {
                    folder += File.separator + FileUtils.getNow(now);
                }
                folder += File.separator + "registries";
                if (ConfigUtils.doDumpFileOrganizeFolderByType()) {
                    folder += File.separator + normalizeIdPath(reg.getKey().getValue());
                }
                FileUtils.writeEntries(folder, reg.getKey().getValue(), entries);
            } catch (IOException e) {
                err.add(reg.getKey().getValue());
                LOGGER.error("An error occurred trying to dump registry {}", reg.getKey().getValue(), e);
                i.incrementAndGet();
            }
        }
        if (i.get() > 0) {
            FileUtils.writeErrors(Map.of("Registries", err));
        }
        lock.unlock();
        return i.get();
    }

    private static Map<String, Set<Identifier>> dumpTags(World world, LocalDateTime now, AtomicInteger i) {
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

    private static RecipeJsonParser resolveRecipeParser(Recipe<?> recipe, Identifier id, RecipeJsonParser parser) {
        if (!parser.getClass().isAnnotationPresent(TargetRecipeType.class)) return null;
        TargetRecipeType type = parser.getClass().getAnnotation(TargetRecipeType.class);
        for (String s : type.supports()) {
            if (id.equals(Identifier.tryParse(s)) && parser.in(recipe) == RecipeJsonParser.InResult.SUCCESS) {
                return parser;
            }
        }
        return null;
    }

    private static RecipeJsonParser getRecipeParser(Recipe<?> recipe) {
        Identifier id = Registry.RECIPE_TYPE.getId(recipe.getType());
        if (id == null) {
            return null;
        }
        RecipeJsonParser parser = RECIPE_PARSERS.get(id);
        if (parser == null) {
            for (RecipeJsonParser p : RECIPE_PARSERS.values()) {
                parser = resolveRecipeParser(recipe, id, p);
                if (parser != null) {
                    break;
                }
            }
        }
        return parser;
    }

    private static int processRecipe(Recipe<?> recipe, RecipeJsonParser parser, LocalDateTime now, AtomicInteger i) {
        if (parser == null) return 1;
        try {
          RecipeJsonParser.InResult result = parser.in(recipe);
          if (result == RecipeJsonParser.InResult.FAILURE) return 2;
          if (result == RecipeJsonParser.InResult.IGNORED) return -1;
          JsonObject o = parser.toJson();
          if (o == null) return 2;
          // noinspection OptionalGetWithoutIsPresent
          Identifier type = RECIPE_PARSERS.keySet().stream().filter(k -> RECIPE_PARSERS.get(k).equals(parser)).findFirst().get();
          Identifier id = Optional.ofNullable(parser.alternativeId()).orElse(recipe.getId());
          FileUtils.storeRecipe(o, id, type, now, parser.isSpecial(), i);
        } catch (Exception e) {
            if (ConfigUtils.doErrorPrintStacktrace()) {
                LOGGER.error("An error occurred while trying to dump recipe {}", recipe.getId(), e);
            }
            return 2;
        }
        return 0;
    }

    private static Map<String, Set<Identifier>> dumpRecipes(World world, LocalDateTime now, AtomicInteger i) {
        Set<Identifier> nonParsableTypes = new HashSet<>();
        Set<Identifier> erroredRecipes = new HashSet<>();
        world.getRecipeManager().values().forEach(recipe -> {
            Identifier id = Registry.RECIPE_TYPE.getId(recipe.getType());
            RecipeJsonParser parser = getRecipeParser(recipe);
            int r = processRecipe(recipe, parser, now, i);
            if (r == 2) erroredRecipes.add(recipe.getId());
            else if (r == 1) nonParsableTypes.add(id);
        });
        RECIPE_PARSERS.values().forEach(RecipeJsonParser::cycle);
        nonParsableTypes.forEach(e -> LOGGER.error("Unable to parse recipes of type {}", e));
        i.addAndGet(erroredRecipes.size() + nonParsableTypes.size());
        Map<String, Set<Identifier>> out = new HashMap<>();
        if (!nonParsableTypes.isEmpty()) out.put("Recipe Types", nonParsableTypes);
        if (!erroredRecipes.isEmpty()) out.put("Recipes", erroredRecipes);
        return out;
    }

    private static Map<String, Set<Identifier>> dumpLootTables(ServerWorld world, LocalDateTime now, AtomicInteger i) {
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

    private static Map<String, Set<Identifier>> dumpAdvancements(ServerWorld world, LocalDateTime now, AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        world.getServer().getAdvancementLoader().getAdvancements().forEach(adv -> {
            JsonObject o = JsonUtils.advancementToJson(adv);
            if (FileUtils.storeAdvancement(o, adv.getId(), now, i)) {
                err.add(adv.getId());
            }
        });
        if (!err.isEmpty()) return Map.of("Advancements", err);
        return Map.of();
    }

    private static Map<String, Set<Identifier>> dumpDimensions(ServerWorld world, LocalDateTime now, AtomicInteger i) {
        Set<Identifier> err = new HashSet<>();
        world.getServer().getWorlds().forEach(w -> {
            if (dumpDim(w, now, i)) {
                err.add(w.getDimensionKey().getValue());
            }
        });
        if (!err.isEmpty()) return Map.of("Dimension Types", err);
        return Map.of();
    }

    private static boolean dumpDim(World world, LocalDateTime now, AtomicInteger i) {
        DimensionType dim = world.getDimension();
        return FileUtils.storeDimension(JsonUtils.dimensionJson(dim), world.getDimensionKey().getValue(), now, i);
    }

    private static Map<String, Set<Identifier>> dumpFunctions(ServerWorld world, LocalDateTime now, AtomicInteger i) {
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

    private static Map<String, Set<Identifier>> dumpStructureTemplates(ServerWorld world, LocalDateTime now, AtomicInteger i) {
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

    public static int dumpData(World world, LocalDateTime now, boolean tags, boolean recipes, boolean tables,
                               boolean advancements, boolean dimensionTypes, boolean functions, boolean templates) {
        lock.lock();
        AtomicInteger i = new AtomicInteger();
        Map<String, Set<Identifier>> errMap = new LinkedHashMap<>();
        if (tags && ConfigUtils.doDataDumpTags()) {
            errMap.putAll(dumpTags(world, now, i));
        }
        if (recipes && ConfigUtils.doDataDumpRecipes()) {
            errMap.putAll(dumpRecipes(world, now, i));
        }
        if (world instanceof ServerWorld w) {
            if (tables && ConfigUtils.doDumpLootTables()) {
                errMap.putAll(dumpLootTables(w, now, i));
            }
            if (advancements && ConfigUtils.doDumpAdvancements()) {
                errMap.putAll(dumpAdvancements(w, now, i));
            }
            if (dimensionTypes && ConfigUtils.doDumpDimensionTypes()) {
                errMap.putAll(dumpDimensions(w, now, i));
            }
            if (functions && ConfigUtils.doDumpFunctions()) {
                errMap.putAll(dumpFunctions(w, now, i));
            }
            if (templates && ConfigUtils.doDumpStructureTemplates()) {
                errMap.putAll(dumpStructureTemplates(w, now, i));
            }
        } else {
            if (dimensionTypes && ConfigUtils.doDumpDimensionTypes() && dumpDim(world, now, i)) {
                errMap.put("Dimension Types", Set.of(world.getDimensionKey().getValue()));
            }
        }
        if (i.get() > 0) {
            FileUtils.writeErrors(errMap);
        }
        lock.unlock();
        return i.get();
    }

    public static int dumpData(World world, LocalDateTime now) {
        return dumpData(world, now, true, true, true, true, true, true, true);
    }

    public static void debug() {
        if (!ConfigUtils.isDebugEnabled()) return;
        FileUtils.writeDebug(REGISTRIES, RECIPE_PARSERS);
    }

    public static String normalizeId(Identifier id) {
        return id.getNamespace() + File.separator + normalizeIdPath(id);
    }

    public static String normalizeIdPath(Identifier id) {
        return String.join(File.separator, id.getPath().split("/"));
    }

    private Utils() {}
}
