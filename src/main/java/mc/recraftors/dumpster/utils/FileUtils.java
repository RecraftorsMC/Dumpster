package mc.recraftors.dumpster.utils;

import com.google.gson.*;
import mc.recraftors.dumpster.parsers.recipes.RecipeJsonParser;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public final class FileUtils {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String JSON_EXT = ".json";

    private static void err(String target, Identifier id, Exception e) {
        if (ConfigUtils.doErrorPrintStacktrace()) {
            Utils.LOGGER.error("An error occurred trying to dump {} {}", target, id, e);
        }
    }

    public static String getNow() {
        return getNow(LocalDateTime.now());
    }

    public static String getNow(LocalDateTime now) {
        return now.format(DateTimeFormatter.ofPattern("uuuu-MM-dd-HH-mm-ss"));
    }

    public static String singleNameIdPath(Identifier id) {
        return id.getNamespace() + "_" + String.join("-", id.getPath().split("/"));
    }

    public static StringBuilder pathBuilder(LocalDateTime now, String target, Identifier type) {
        return pathBuilder(now, target, singleNameIdPath(type));
    }

    public static StringBuilder pathBuilder(LocalDateTime now, String target, String type) {
        StringBuilder builder = new StringBuilder(ConfigUtils.dumpFileMainFolder());
        if (ConfigUtils.doDumpFileOrganizeFolderByDate()) {
            builder.append(File.separator).append(getNow(now));
        }
        builder.append(File.separator).append(target);
        if (ConfigUtils.doDumpFileOrganizeFolderByType()) {
            builder.append(File.separator).append(type);
        }
        return builder;
    }

    public static void clearIfNeeded() {
        Path path = Path.of("dump");
        try {
            if (ConfigUtils.doDumpFileClearBeforeDump() && Files.exists(path)) {
                if (Files.isDirectory(path)) {
                    Stream<Path> stream = Files.list(path);
                    for (Path c : stream.toList()) {
                        Files.delete(c);
                    }
                    stream.close();
                } else Files.delete(path);
            }
        } catch (IOException e) {
            Utils.LOGGER.error("An error occurred trying to clear previous dump files", e);
        }
    }

    public static void writeEntries(String folder, Identifier name, Collection<RegistryEntry> entries)
            throws IOException {
        File f = new File(folder);
        Files.createDirectories(f.toPath());
        String s = String.format("%s.json", Utils.normalizeId(name));
        File target = new File(f,s);
        Files.createDirectories(target.getParentFile().toPath());
        FileWriter writer = new FileWriter(target);
        JsonObject object = new JsonObject();
        object.add("size", new JsonPrimitive(entries.size()));
        JsonArray array = new JsonArray();
        entries.forEach(e -> array.add(e.toJson()));
        object.add("values", array);
        writer.write(GSON.toJson(object));
        writer.flush();
        writer.close();
    }

    static void storeTag(Collection<RegistryEntry> entries, Identifier id, Identifier name, LocalDateTime now, AtomicInteger i) {
        try {
            StringBuilder builder = pathBuilder(now, "tags", name);
            builder.append(File.separator).append(Utils.normalizeIdPath(id));
            writeEntries(builder.toString(), name, entries);
        } catch (IOException e) {
            err("tag "+id, name, e);
            i.incrementAndGet();
        }
    }

    static void storeRaw(String s1, String s2) throws IOException {
        File f = new File(s2);
        Files.createDirectories(f.getParentFile().toPath());
        try (FileWriter w = new FileWriter(f)) {
            w.write(s1);
        }
    }

    static void storeJson(JsonObject e, String s) throws IOException {
        storeRaw(GSON.toJson(e), s);
    }

    static void storeNbt(NbtElement e, String s) throws IOException {
        File f = new File(s);
        Files.createDirectories(f.getParentFile().toPath());
        try (DataOutputStream stream = new DataOutputStream(new FileOutputStream(s))) {
            e.write(stream);
        }
    }

    static void storeRecipe(JsonObject object, Identifier id, Identifier type, LocalDateTime now, boolean isSpecial, AtomicInteger i) {
        try {
            StringBuilder builder = new StringBuilder(ConfigUtils.dumpFileMainFolder());
            if (ConfigUtils.doDumpFileOrganizeFolderByDate()) {
                builder.append(File.separator).append(getNow(now));
            }
            builder.append(File.separator).append("recipes");
            if (isSpecial) {
                builder.append(File.separator).append("special");
            }
            if (ConfigUtils.doDumpFileOrganizeFolderByType()) {
                builder.append(File.separator).append(singleNameIdPath(type));
            }
            builder.append(File.separator).append(Utils.normalizeIdPath(id)).append(JSON_EXT);
            storeJson(object, builder.toString());
        } catch (IOException e) {
            err("recipe", id, e);
            i.incrementAndGet();
        }
    }

    static void storeLootTable(JsonObject elem, Identifier id, LocalDateTime now, AtomicInteger i) {
        try {
            StringBuilder builder = pathBuilder(now, "loot_tables", id.getNamespace());
            builder.append(File.separator).append(Utils.normalizeIdPath(id)).append(JSON_EXT);
            storeJson(elem, builder.toString());
        } catch (IOException e) {
            err("loot table", id, e);
            i.incrementAndGet();
        }
    }

    static boolean storeAdvancement(JsonObject o, Identifier id, LocalDateTime now, AtomicInteger i) {
        try {
            StringBuilder builder = pathBuilder(now, "advancements", id.getNamespace());
            builder.append(File.separator).append(Utils.normalizeIdPath(id)).append(JSON_EXT);
            storeJson(o, builder.toString());
            return false;
        } catch (IOException e) {
            err("advancement", id, e);
            i.incrementAndGet();
            return true;
        }
    }

    static boolean storeDimension(JsonObject o, Identifier id, LocalDateTime now, AtomicInteger i) {
        try {
            StringBuilder builder = pathBuilder(now, "dimension_type", id.getNamespace());
            builder.append(File.separator).append(Utils.normalizeIdPath(id)).append(JSON_EXT);
            storeJson(o, builder.toString());
            return false;
        } catch (IOException e) {
            err("dimension type", id, e);
            i.incrementAndGet();
            return true;
        }
    }

    static boolean storeFunction(String f, Identifier id, LocalDateTime now, AtomicInteger i) {
        try {
            StringBuilder builder = pathBuilder(now, "functions", id.getNamespace());
            builder.append(File.separator).append(Utils.normalizeIdPath(id)).append(".mcfunction");
            storeRaw(f, builder.toString());
            return false;
        } catch (IOException e) {
            err("function", id, e);
            i.incrementAndGet();
            return true;
        }
    }

    static boolean storeStructureTemplate(NbtElement nbt, Identifier id, LocalDateTime now, AtomicInteger i) {
        try {
            StringBuilder builder = pathBuilder(now, "structures", id.getNamespace());
            builder.append(File.separator).append(Utils.normalizeIdPath(id)).append(".nbt");
            storeNbt(nbt, builder.toString());
            return false;
        } catch (IOException e) {
            err("structure_template", id, e);
            i.incrementAndGet();
            return true;
        }
    }

    static void writeErrors(Map<String, Set<Identifier>> setMap) {
        try {
            Path p = Path.of(ConfigUtils.dumpFileMainFolder(), "errors.txt");
            Files.createDirectories(p.getParent());
            FileWriter writer = new FileWriter(p.toFile(), true);
            writer.write(String.format(" ======\tError report %s\t======", getNow()));
            for (Map.Entry<String, Set<Identifier>> entry : setMap.entrySet()) {
                writer.write(String.format("%n%n\t### %s ###", entry.getKey()));
                for (Identifier id : entry.getValue()) {
                    writer.write(String.format("%n\t - %s", id));
                }
            }
            writer.write("\n\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Utils.LOGGER.error("An exception occurred trying to log dump errors", e);
        }
    }

    static void writeDebug(@NotNull Collection<Registry<?>> registries, Map<Identifier, RecipeJsonParser> recipeParsers) {
        try {
            Path p = Path.of(ConfigUtils.dumpFileMainFolder(), "debug.txt");
            Files.createDirectories(p.getParent());
            FileWriter writer = new FileWriter(p.toFile(), true);
            writer.write(String.format(" ======\tDebug report %s\t======%n", getNow()));
            writer.write(String.format("%n\t### Registries ###%n"));
            for (Registry<?> reg : registries.stream().sorted(Comparator.comparing(e -> e.getKey().getValue())).toList()) {
                writer.write(String.format("%n\t - %s", reg.getKey().getValue()));
            }
            writer.write(String.format("%n%n\t### Recipe parsers ###%n"));
            int min = 0;
            for (Identifier id : recipeParsers.keySet()) {
                min = Math.max(min, id.toString().length());
            }
            for (Map.Entry<Identifier, RecipeJsonParser> entry : recipeParsers.entrySet()) {
                writer.write(String.format("%n\t - %-"+min+"s\t@\t%s", entry.getKey(), entry.getValue().getClass()));
            }
            writer.write("\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Utils.LOGGER.error("An error occurred trying to write {} debug", Utils.MOD_ID, e);
        }
    }

    private FileUtils() {}
}
