package mc.recraftors.dumpster.utils;

import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class ConfigUtils {
    private ConfigUtils() {}

    private static final Path PATH;
    private static final Properties PROPERTIES;

    private static final String FALSE = "false";
    private static final String TRUE = "true";
    private static final String COMMENTS = """
            Properties file for the Dumpster mod
            Dumping can either be done automatically or via command ("/dump")
            The command will exist both on the server, to store directly on the server's files, and on the client""";
    private static final String STARTUP_REG_DUMP = "autoDumpRegistries.onStartup";
    private static final String RELOAD_REG_DUMP = "autoDumpRegistries.onReload";
    private static final String RELOAD_DATA_DUMP = "autoDumpResources.onReload";
    private static final String DATA_DUMP_TAGS = "dump.data.dumpTags";
    private static final String DATA_DUMP_RECIPES = "dump.data.dumpRecipes";
    private static final String DATA_DUMP_LOOT_TABLES = "dump.data.dumpLootTables";
    private static final String DUMP_MAIN_FOLDER = "dumpFile.mainFolder";
    private static final String DUMP_ORG_DATE = "dumpFile.organizeFolderByDate";
    private static final String DUMP_ORG_TYPE = "dumpFile.organizeFolderByType";

    static {
        PATH = Path.of(
                FabricLoader.getInstance().getConfigDir().toString(),
                String.format("%s.properties", Utils.MOD_ID)
        );

        Properties defaults = new Properties();
        defaults.setProperty(STARTUP_REG_DUMP, FALSE);
        defaults.setProperty(RELOAD_REG_DUMP, FALSE);
        defaults.setProperty(RELOAD_DATA_DUMP, FALSE);
        defaults.setProperty(DATA_DUMP_TAGS, TRUE);
        defaults.setProperty(DATA_DUMP_RECIPES, TRUE);
        defaults.setProperty(DATA_DUMP_LOOT_TABLES, TRUE);
        defaults.setProperty(DUMP_MAIN_FOLDER, "dump");
        defaults.setProperty(DUMP_ORG_DATE, FALSE);
        defaults.setProperty(DUMP_ORG_TYPE, TRUE);

        PROPERTIES = new Properties(defaults);
        PROPERTIES.setProperty(STARTUP_REG_DUMP, FALSE);
        PROPERTIES.setProperty(RELOAD_REG_DUMP, FALSE);
        PROPERTIES.setProperty(RELOAD_DATA_DUMP, FALSE);
        PROPERTIES.setProperty(DATA_DUMP_TAGS, TRUE);
        PROPERTIES.setProperty(DATA_DUMP_RECIPES, TRUE);
        PROPERTIES.setProperty(DATA_DUMP_LOOT_TABLES, TRUE);
        PROPERTIES.setProperty(DUMP_MAIN_FOLDER, "dump");
        PROPERTIES.setProperty(DUMP_ORG_DATE, FALSE);
        PROPERTIES.setProperty(DUMP_ORG_TYPE, TRUE);

        try {
            if (!(Files.exists(PATH) && Files.isRegularFile(PATH) && Files.isReadable(PATH))) {
                if (PATH.toFile().createNewFile())
                    Utils.LOGGER.info("Created path to {} config file", Utils.MOD_ID);
                PROPERTIES.store(new FileWriter(PATH.toFile()), COMMENTS);
            } else {
                PROPERTIES.load(new FileReader(PATH.toFile()));
                validateTargetPath();
            }
        } catch (IOException e) {
            Utils.LOGGER.error(e);
        }
    }

    public static boolean doAutoDumpRegistriesOnStartup() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(STARTUP_REG_DUMP));
    }

    public static boolean doAutoDumpRegistriesOnReload() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(RELOAD_REG_DUMP));
    }

    public static boolean doAutoDumpResourcesOnReload() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(RELOAD_DATA_DUMP));
    }

    public static boolean doDataDumpTags() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DATA_DUMP_TAGS));
    }

    public static boolean doDataDumpRecipes() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DATA_DUMP_RECIPES));
    }

    public static boolean doDumpLootTables() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DATA_DUMP_LOOT_TABLES));
    }

    public static String dumpFileMainFolder() {
        return PROPERTIES.getProperty(DUMP_MAIN_FOLDER);
    }

    public static boolean doDumpFileOrganizeFolderByDate() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DUMP_ORG_DATE));
    }

    public static boolean doDumpFileOrganizeFolderByType() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DUMP_ORG_TYPE));
    }

    public static boolean reload() throws IOException {
        boolean b = Files.exists(PATH) && Files.isRegularFile(PATH) && Files.isReadable(PATH);
        try (FileReader reader = new FileReader(PATH.toFile())) {
            PROPERTIES.load(reader);
        }
        validateTargetPath();
        return b;
    }

    public static void save() throws IOException {
        try (FileWriter writer = new FileWriter(PATH.toFile())) {
            PROPERTIES.store(writer, COMMENTS);
        }
    }

    private static void validateTargetPath() {
        String s = dumpFileMainFolder();
        if (s.contains(File.pathSeparator) || s.contains("/") || s.contains("..")) {
            PROPERTIES.setProperty(DUMP_MAIN_FOLDER, "dump");
        }
    }
}
