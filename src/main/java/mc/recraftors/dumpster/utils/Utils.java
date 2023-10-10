package mc.recraftors.dumpster.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import mc.recraftors.dumpster.recipes.RecipeJsonParser;
import mc.recraftors.dumpster.recipes.TargetRecipeType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
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

    private static Map<String, Set<Identifier>> dumpRecipes(World world, LocalDateTime now, AtomicInteger i) {
        Set<Identifier> nonParsableTypes = new HashSet<>();
        Set<Identifier> erroredRecipes = new HashSet<>();
        world.getRecipeManager().values().forEach(recipe -> {
            Identifier id = Registry.RECIPE_TYPE.getId(recipe.getType());
            RecipeJsonParser parser = getRecipeParser(recipe);
            if (parser == null) {
                nonParsableTypes.add(id);
                return;
            }
            try {
                RecipeJsonParser.InResult result = parser.in(recipe);
                if (result == RecipeJsonParser.InResult.FAILURE) {
                    erroredRecipes.add(recipe.getId());
                }
                if (result != RecipeJsonParser.InResult.SUCCESS) {
                    return;
                }
                JsonObject o = parser.toJson();
                if (o == null) {
                    erroredRecipes.add(recipe.getId());
                    return;
                }
                //noinspection OptionalGetWithoutIsPresent
                id = RECIPE_PARSERS.keySet().stream().filter(k -> RECIPE_PARSERS.get(k).equals(parser)).findFirst().get();
                FileUtils.storeRecipe(o, recipe.getId(), id, now, parser.isSpecial(), i);
            } catch (Exception e) {
                erroredRecipes.add(recipe.getId());
                if (ConfigUtils.doErrorPrintStacktrace()) {
                    LOGGER.error("An error occurred while trying to dump recipe {}", recipe.getId(), e);
                }
            }
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

    public static int dumpData(World world, LocalDateTime now) {
        AtomicInteger i = new AtomicInteger();
        Map<String, Set<Identifier>> errMap = new LinkedHashMap<>();
        if (ConfigUtils.doDataDumpTags()) {
            errMap.putAll(dumpTags(world, now, i));
        }
        if (ConfigUtils.doDataDumpRecipes()) {
            errMap.putAll(dumpRecipes(world, now, i));
        }
        if (ConfigUtils.doDumpLootTables() && world instanceof ServerWorld s) {
            errMap.putAll(dumpLootTables(s, now, i));
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
