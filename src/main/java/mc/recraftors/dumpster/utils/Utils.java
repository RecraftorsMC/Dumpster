package mc.recraftors.dumpster.utils;

import com.google.gson.JsonObject;
import mc.recraftors.dumpster.recipes.RecipeJsonParser;
import mc.recraftors.dumpster.recipes.TargetRecipeType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
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
        Map<Identifier, RecipeJsonParser> parserMap = new HashMap<>();
        FabricLoader.getInstance().getEntrypoints("recipe-dump", RecipeJsonParser.class).forEach(e -> {
            if (!e.getClass().isAnnotationPresent(TargetRecipeType.class)) return;
            TargetRecipeType type = e.getClass().getAnnotation(TargetRecipeType.class);
            Identifier id = Identifier.tryParse(type.value());
            if (id == null) return;
            if (parserMap.containsKey(id)) {
                RecipeJsonParser o = parserMap.get(id);
                if (!o.getClass().isAnnotationPresent(TargetRecipeType.class)) return;
                if (o.getClass().getAnnotation(TargetRecipeType.class).priority() < type.priority()){
                    parserMap.put(id, e);
                }
            } else {
                parserMap.put(id, e);
            }
        });
        RECIPE_PARSERS = parserMap;
    }

    public static void reg(Registry<?> reg) {
        REGISTRIES.add(reg);
    }

    public static int dumpRegistries() {
        AtomicInteger i = new AtomicInteger();
        for (Registry<?> reg : REGISTRIES) {
            if (reg == null) {
                i.incrementAndGet();
                continue;
            }
            Collection<RegistryEntry> entries = new ArrayList<>();
            reg.getEntrySet().forEach(entry -> {
                Identifier id = entry.getKey().getValue();
                Class<?> tClass = entry.getValue().getClass();
                String s = entry.getKey().toString();
                RegistryEntry r = new RegistryEntry(id, tClass, s);
                entries.add(r);
            });
            try {
                String folder = ConfigUtils.dumpFileMainFolder() + File.separator + "registries";
                if (ConfigUtils.doDumpFileOrganizeFolderByType()) {
                    folder += File.separator + normalizeIdPath(reg.getKey().getValue());
                }
                if (ConfigUtils.doDumpFileOrganizeFolderByDate()) {
                    folder += File.separator + FileUtils.getNow();
                }
                FileUtils.writeEntries(folder, reg.getKey().getValue(), entries);
            } catch (IOException e) {
                LOGGER.error("An error occurred trying to dump registry {}", reg.getKey().getValue(), e);
                i.incrementAndGet();
            }
        }
        return i.get();
    }

    private static void dumpTags(World world, AtomicInteger i) {
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
                    FileUtils.storeTag(entries, reg.getKey().getValue(), id, i);
                });
            } catch (IllegalStateException e) {
                i.incrementAndGet();
            }
        }
    }

    private static void dumpRecipes(World world, AtomicInteger i) {
        Set<Identifier> unparsableTypes = new HashSet<>();
        Set<Identifier> erroredRecipes = new HashSet<>();
        world.getRecipeManager().values().forEach(recipe -> {
            Identifier id = Registry.RECIPE_TYPE.getId(recipe.getType());
            if (!RECIPE_PARSERS.containsKey(id)) {
                unparsableTypes.add(id);
                return;
            }
            try {
                RecipeJsonParser parser = RECIPE_PARSERS.get(id);
                parser.in(recipe);
                JsonObject o = parser.toJson();
                if (o == null) {
                    erroredRecipes.add(recipe.getId());
                }
                FileUtils.storeRecipe(o, recipe.getId(), id, parser.isSpecial(), i);
            } catch (Exception e) {
                erroredRecipes.add(recipe.getId());
            }
        });
        i.addAndGet(erroredRecipes.size() + unparsableTypes.size());
    }

    private static void dumpLootTables(ServerWorld world, AtomicInteger i) {
        LootManager manager = world.getServer().getLootManager();
        manager.getTableIds().forEach(id -> {
            LootTable table = manager.getTable(id);
            //TODO: implement loot context parsing system similar to the recipe one
        });
    }

    public static int dumpData(World world) {
        AtomicInteger i = new AtomicInteger();
        if (ConfigUtils.doDataDumpTags()) {
            dumpTags(world, i);
        }
        if (ConfigUtils.doDataDumpRecipes()) {
            dumpRecipes(world, i);
        }
        if (ConfigUtils.doDumpLootTables() && world instanceof ServerWorld s) {
            dumpLootTables(s, i);
        }
        return i.get();
    }

    public static String normalizeId(Identifier id) {
        return id.getNamespace() + File.separator + normalizeIdPath(id);
    }

    public static String normalizeIdPath(Identifier id) {
        return String.join(File.separator, id.getPath().split("/"));
    }

    private Utils() {}
}
