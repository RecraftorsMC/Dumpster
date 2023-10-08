package mc.recraftors.dumpster.utils;

import com.google.gson.*;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public final class FileUtils {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static String getNow() {
        return getNow(LocalDateTime.now());
    }

    public static String getNow(LocalDateTime now) {
        return now.format(DateTimeFormatter.ofPattern("uuuu-MM-dd-kk-mm-ss"));
    }

    public static String singleNameIdPath(Identifier id) {
        return id.getNamespace() + "_" + String.join("-", id.getPath().split("/"));
    }

    public static void clearIfNeeded() {
        Path path = Path.of("dump");
        try {
            if (ConfigUtils.doDumpFileClearBeforeDump() && Files.exists(path)) {
                if (Files.isDirectory(path)) {
                    for (Path c : Files.list(path).toList()) {
                        Files.delete(c);
                    }
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
            StringBuilder builder = new StringBuilder(ConfigUtils.dumpFileMainFolder());
            if (ConfigUtils.doDumpFileOrganizeFolderByDate()) {
                builder.append(File.separator).append(getNow(now));
            }
            builder.append(File.separator).append("tags");
            if (ConfigUtils.doDumpFileOrganizeFolderByType()) {
                builder.append(File.separator).append(name.getNamespace());
            }
            builder.append(File.separator).append(Utils.normalizeIdPath(id));
            writeEntries(builder.toString(), name, entries);
        } catch (IOException e) {
            Utils.LOGGER.error("An error occurred trying to dump tag {} / {}", id, name, e);
            i.incrementAndGet();
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
            builder.append(File.separator).append(Utils.normalizeIdPath(id)).append(".json");
            File f = new File(builder.toString());
            Files.createDirectories(f.getParentFile().toPath());
            FileWriter writer = new FileWriter(f);
            writer.write(GSON.toJson(object));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Utils.LOGGER.error("An error occurred trying to dump data {}", id);
            i.incrementAndGet();
        }
    }

    static void writeErrors(Map<String, Set<Identifier>> setMap) {
        try {
            Path p = Path.of(ConfigUtils.dumpFileMainFolder(), "errors.txt");
            Files.createDirectories(p.getParent());
            FileWriter writer = new FileWriter(p.toFile(), true);
            writer.write(String.format(" ======\tError report %s\t======%n", getNow()));
            for (Map.Entry<String, Set<Identifier>> entry : setMap.entrySet()) {
                writer.write(String.format("%n%n\t### %s ###", entry.getKey()));
                for (Identifier id : entry.getValue()) {
                    writer.write(String.format("%n\t - %s", id));
                }
            }
            writer.write("\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Utils.LOGGER.error("An exception occurred trying to log dump errors", e);
        }
    }

    private FileUtils() {}
}
