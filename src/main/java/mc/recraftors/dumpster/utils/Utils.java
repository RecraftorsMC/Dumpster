package mc.recraftors.dumpster.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.loot_tables.functions.LootFunctionJsonParser;
import mc.recraftors.dumpster.loot_tables.functions.TargetLootFunctionType;
import mc.recraftors.dumpster.recipes.RecipeJsonParser;
import mc.recraftors.dumpster.recipes.TargetRecipeType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class Utils {
    public static final String MOD_ID = "dumpster";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final String ERROR_TOAST_TITLE = "dumpster.toast.error.title";
    public static final String ERROR_TOAST_DESC = "dumpster.toast.error.desc";

    private static final Collection<Registry<?>> REGISTRIES = new LinkedHashSet<>();
    private static final Map<Identifier, RecipeJsonParser> RECIPE_PARSERS;
    private static final Map<Identifier, LootFunctionJsonParser> LOOT_FUNCTION_PARSERS;

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
        Map<Identifier, LootFunctionJsonParser> lootFunctionParserMap = new HashMap<>();
        FabricLoader.getInstance().getEntrypoints("loot-context-dump", LootFunctionJsonParser.class).forEach(e -> {
            if (!e.getClass().isAnnotationPresent(TargetLootFunctionType.class)) return;
            TargetLootFunctionType type = e.getClass().getAnnotation(TargetLootFunctionType.class);
            Identifier id = Identifier.tryParse(type.value());
            if (id == null) return;
            if (lootFunctionParserMap.containsKey(id)) {
                LootFunctionJsonParser o = lootFunctionParserMap.get(id);
                if (!o.getClass().isAnnotationPresent(TargetLootFunctionType.class) ||
                        o.getClass().getAnnotation(TargetLootFunctionType.class).priority() < type.priority()) {
                    lootFunctionParserMap.put(id, e);
                }
            } else {
                lootFunctionParserMap.put(id, e);
            }
        });
        LOOT_FUNCTION_PARSERS = lootFunctionParserMap;
    }

    public static void reg(Registry<?> reg) {
        if (reg == null) {
            return;
        }
        REGISTRIES.add(reg);
    }

    public static int dumpRegistries(LocalDateTime now) {
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
        return i.get();
    }

    private static Map<String, Set<Identifier>> dumpTags(World world, AtomicInteger i) {
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
                    FileUtils.storeTag(entries, reg.getKey().getValue(), id, LocalDateTime.now(), i);
                });
            } catch (IllegalStateException e) {
                i.incrementAndGet();
                err.add(reg.getKey().getValue());
            }
        }
        return Map.of("Tags", err);
    }

    private static RecipeJsonParser getParser(Recipe<?> recipe) {
        Identifier id = Registry.RECIPE_TYPE.getId(recipe.getType());
        if (id == null) {
            return null;
        }
        RecipeJsonParser parser = RECIPE_PARSERS.get(id);
        if (parser == null) {
            for (RecipeJsonParser p : RECIPE_PARSERS.values()) {
                if (!p.getClass().isAnnotationPresent(TargetRecipeType.class)) continue;
                TargetRecipeType type = p.getClass().getAnnotation(TargetRecipeType.class);
                for (String s : type.supports()) {
                    if (id.equals(Identifier.tryParse(s)) && p.in(recipe) == RecipeJsonParser.InResult.SUCCESS) {
                        parser = p;
                        break;
                    }
                }
                if (parser == null) {
                    break;
                }
            }
        }
        return parser;
    }

    private static Map<String, Set<Identifier>> dumpRecipes(World world, LocalDateTime now, AtomicInteger i) {
        Set<Identifier> unparsableTypes = new HashSet<>();
        Set<Identifier> erroredRecipes = new HashSet<>();
        world.getRecipeManager().values().forEach(recipe -> {
            Identifier id = Registry.RECIPE_TYPE.getId(recipe.getType());
            RecipeJsonParser parser = getParser(recipe);
            if (parser == null) {
                unparsableTypes.add(id);
                return;
            }
            try {
                parser.in(recipe);
                JsonObject o = parser.toJson();
                if (o == null) {
                    erroredRecipes.add(recipe.getId());
                }
                FileUtils.storeRecipe(o, recipe.getId(), id, now, parser.isSpecial(), i);
            } catch (Exception e) {
                erroredRecipes.add(recipe.getId());
            }
        });
        RECIPE_PARSERS.values().forEach(RecipeJsonParser::cycle);
        unparsableTypes.forEach(e -> LOGGER.error("Unable to parse recipes of type {}", e));
        erroredRecipes.forEach(e -> LOGGER.error("An error occurred while trying to dump recipe {}", e));
        i.addAndGet(erroredRecipes.size() + unparsableTypes.size());
        Map<String, Set<Identifier>> out = new HashMap<>();
        if (!unparsableTypes.isEmpty()) out.put("Recipe Types", unparsableTypes);
        if (!erroredRecipes.isEmpty()) out.put("Recipes", erroredRecipes);
        return out;
    }

    private static void dumpLootTables(ServerWorld world, AtomicInteger i) {
        Set<Identifier> unparsableFunctions = new HashSet<>();
        LootManager manager = world.getServer().getLootManager();
        manager.getTableIds().forEach(id -> {
            LootTable table = manager.getTable(id);
            Identifier type = LootContextTypes.getId(table.getType());
            JsonObject main = new JsonObject();
            main.add("type", new JsonPrimitive(type.toString()));
            //TODO: implement loot context parsing system similar to the recipe one
        });
    }

    public static int dumpData(World world, LocalDateTime now) {
        AtomicInteger i = new AtomicInteger();
        Map<String, Set<Identifier>> errMap = new LinkedHashMap<>();
        if (ConfigUtils.doDataDumpTags()) {
            errMap.putAll(dumpTags(world, i));
        }
        if (ConfigUtils.doDataDumpRecipes()) {
            errMap.putAll(dumpRecipes(world, now, i));
        }
        if (ConfigUtils.doDumpLootTables() && world instanceof ServerWorld s) {
            dumpLootTables(s, i);
        }
        if (i.get() > 0) {
            FileUtils.writeErrors(errMap);
        }
        return i.get();
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
