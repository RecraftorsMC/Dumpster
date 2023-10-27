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
    private static final String DATA_DUMP_ADVANCEMENTS = "dump.data.dumpAdvancements";
    private static final String DATA_DUMP_DIMENSIONS = "dump.data.dumpDimensions";
    private static final String DATA_DUMP_DIM_TYPES = "dump.data.dumpDimensionTypes";
    private static final String DATA_DUMP_FUNCTIONS = "dump.data.dumpMcFunctions";
    private static final String DATA_DUMP_STRUCTURE_TEMPLATES = "dump.data.dumpStructureTemplates";
    private static final String DATA_DUMP_WORLDGEN_BIOMES = "dump.data.dumpWorldgen.biomes";
    private static final String DATA_DUMP_WORLDGEN_CARVERS = "dump.data.dumpWorldgen.carvers";
    private static final String DATA_DUMP_WORLDGEN_CONF_FEATURES = "dump.data.dumpWorldgen.configuredFeatures";
    private static final String DATA_DUMP_WORLDGEN_DENSITY_FUNCTIONS = "dump.data.dumpWorldgen.densityFunctions";
    private static final String DATA_DUMP_WORLDGEN_FLAT_PRESETS = "dump.data.dumpWorldgen.flatPresets";
    private static final String DATA_DUMP_WORLDGEN_NOISE = "dump.data.dumpWorldgen.noise";
    private static final String DATA_DUMP_WORLDGEN_NOISE_SETTINGS = "dump.data.dumpWorldgen.noiseSettings";
    private static final String DATA_DUMP_WORLDGEN_PLACED_FEATURES = "dump.data.dumpWorldgen.placedFeatures";
    private static final String DATA_DUMP_WORLDGEN_PROCESSOR_LISTS = "dump.data.dumpWorldgen.processorLists";
    private static final String DUMP_MAIN_FOLDER = "dumpFile.mainFolder";
    private static final String DUMP_ORG_DATE = "dumpFile.organizeFolderByDate";
    private static final String DUMP_ORG_TYPE = "dumpFile.organizeFolderByType";
    private static final String DUMP_CLEAR = "dumpFile.clearBeforeDump";
    private static final String DEBUG = "debug.enable";
    private static final String ERROR_STACKTRACE = "debug.stacktrace";

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
        defaults.setProperty(DATA_DUMP_ADVANCEMENTS, TRUE);
        defaults.setProperty(DATA_DUMP_DIMENSIONS, TRUE);
        defaults.setProperty(DATA_DUMP_DIM_TYPES, TRUE);
        defaults.setProperty(DATA_DUMP_FUNCTIONS, TRUE);
        defaults.setProperty(DATA_DUMP_STRUCTURE_TEMPLATES, TRUE);
        defaults.setProperty(DATA_DUMP_WORLDGEN_BIOMES, TRUE);
        defaults.setProperty(DATA_DUMP_WORLDGEN_CARVERS, TRUE);
        defaults.setProperty(DATA_DUMP_WORLDGEN_CONF_FEATURES, TRUE);
        defaults.setProperty(DATA_DUMP_WORLDGEN_DENSITY_FUNCTIONS, TRUE);
        defaults.setProperty(DATA_DUMP_WORLDGEN_FLAT_PRESETS, TRUE);
        defaults.setProperty(DATA_DUMP_WORLDGEN_NOISE, TRUE);
        defaults.setProperty(DATA_DUMP_WORLDGEN_NOISE_SETTINGS, TRUE);
        defaults.setProperty(DATA_DUMP_WORLDGEN_PLACED_FEATURES, TRUE);
        defaults.setProperty(DATA_DUMP_WORLDGEN_PROCESSOR_LISTS, TRUE);
        defaults.setProperty(DUMP_MAIN_FOLDER, "dump");
        defaults.setProperty(DUMP_ORG_DATE, FALSE);
        defaults.setProperty(DUMP_ORG_TYPE, TRUE);
        defaults.setProperty(DUMP_CLEAR, FALSE);
        defaults.setProperty(DEBUG, FALSE);
        defaults.setProperty(ERROR_STACKTRACE, FALSE);

        PROPERTIES = new Properties(defaults);
        PROPERTIES.setProperty(STARTUP_REG_DUMP, FALSE);
        PROPERTIES.setProperty(RELOAD_REG_DUMP, FALSE);
        PROPERTIES.setProperty(RELOAD_DATA_DUMP, FALSE);
        PROPERTIES.setProperty(DATA_DUMP_TAGS, TRUE);
        PROPERTIES.setProperty(DATA_DUMP_RECIPES, TRUE);
        PROPERTIES.setProperty(DATA_DUMP_LOOT_TABLES, TRUE);
        PROPERTIES.setProperty(DATA_DUMP_ADVANCEMENTS, TRUE);
        PROPERTIES.setProperty(DATA_DUMP_DIMENSIONS, TRUE);
        PROPERTIES.setProperty(DATA_DUMP_DIM_TYPES, TRUE);
        PROPERTIES.setProperty(DATA_DUMP_FUNCTIONS, TRUE);
        PROPERTIES.setProperty(DATA_DUMP_STRUCTURE_TEMPLATES, TRUE);
        PROPERTIES.setProperty(DATA_DUMP_WORLDGEN_BIOMES, TRUE);
        PROPERTIES.setProperty(DATA_DUMP_WORLDGEN_CARVERS, TRUE);
        PROPERTIES.setProperty(DATA_DUMP_WORLDGEN_CONF_FEATURES, TRUE);
        PROPERTIES.setProperty(DATA_DUMP_WORLDGEN_DENSITY_FUNCTIONS, TRUE);
        PROPERTIES.setProperty(DATA_DUMP_WORLDGEN_FLAT_PRESETS, TRUE);
        PROPERTIES.setProperty(DATA_DUMP_WORLDGEN_NOISE, TRUE);
        PROPERTIES.setProperty(DATA_DUMP_WORLDGEN_NOISE_SETTINGS, TRUE);
        PROPERTIES.setProperty(DATA_DUMP_WORLDGEN_PLACED_FEATURES, TRUE);
        PROPERTIES.setProperty(DATA_DUMP_WORLDGEN_PROCESSOR_LISTS, TRUE);
        PROPERTIES.setProperty(DUMP_MAIN_FOLDER, "dump");
        PROPERTIES.setProperty(DUMP_ORG_DATE, FALSE);
        PROPERTIES.setProperty(DUMP_ORG_TYPE, TRUE);
        PROPERTIES.setProperty(DUMP_CLEAR, FALSE);

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

    public static boolean doDumpAdvancements() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DATA_DUMP_ADVANCEMENTS));
    }

    public static boolean doDumpDimensions() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DATA_DUMP_DIMENSIONS));
    }

    public static boolean doDumpDimensionTypes() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DATA_DUMP_DIM_TYPES));
    }

    public static boolean doDumpFunctions() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DATA_DUMP_FUNCTIONS));
    }

    public static boolean doDumpStructureTemplates() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DATA_DUMP_STRUCTURE_TEMPLATES));
    }

    public static boolean doDumpWorldgenBiomes() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DATA_DUMP_WORLDGEN_BIOMES));
    }

    public static boolean doDumpWorldgenCarvers() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DATA_DUMP_WORLDGEN_CARVERS));
    }

    public static boolean doDumpWorldgenConfiguredFeatures() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DATA_DUMP_WORLDGEN_CONF_FEATURES));
    }

    public static boolean doDumpWorldgenDensityFunctions() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DATA_DUMP_WORLDGEN_DENSITY_FUNCTIONS));
    }

    public static boolean doDumpWorldgenFlatGeneratorPresets() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DATA_DUMP_WORLDGEN_FLAT_PRESETS));
    }

    public static boolean doDumpWorldgenNoise() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DATA_DUMP_WORLDGEN_NOISE));
    }

    public static boolean doDumpWorldgenNoiseSettings() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DATA_DUMP_WORLDGEN_NOISE_SETTINGS));
    }

    public static boolean doDumpWorldgenPlacedFeatures() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DATA_DUMP_WORLDGEN_PLACED_FEATURES));
    }

    public static boolean doDumpWorldgenProcessorLists() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DATA_DUMP_WORLDGEN_PROCESSOR_LISTS));
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

    public static boolean doDumpFileClearBeforeDump() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DUMP_CLEAR));
    }

    public static boolean isDebugEnabled() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(DEBUG));
    }

    public static boolean doErrorPrintStacktrace() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(ERROR_STACKTRACE));
    }

    public static void reload() throws IOException {
        boolean b = Files.exists(PATH) && Files.isRegularFile(PATH) && Files.isReadable(PATH);
        if (!b) {
            try (FileReader reader = new FileReader(PATH.toFile())) {
                PROPERTIES.load(reader);
            }
        } else {
            save();
        }
        validateTargetPath();
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
