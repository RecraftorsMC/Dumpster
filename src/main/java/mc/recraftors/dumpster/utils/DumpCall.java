package mc.recraftors.dumpster.utils;

public record DumpCall(boolean registries, boolean data, Data dataO) {
    public static final DumpCall ALL_TRUE = new DumpCall(true, true, Data.ALL_TRUE);
    public record Data(boolean advancements, boolean dimensions, boolean dimensionTypes, boolean functions,
                       boolean lootTables, boolean recipes, boolean structures, boolean tags, boolean worldgen,
                       Worldgen worldgenO) {
        public static final Data ALL_TRUE = new Data(true, true, true, true ,true, true, true, true, true, Worldgen.ALL_TRUE);
    }
    public record Worldgen(boolean biomes, boolean carvers, boolean features, boolean densityFunctions,
                           boolean placedFeature, boolean processorList, boolean structure, boolean structureSet,
                           boolean templatePool) {
        public static final Worldgen ALL_TRUE = new Worldgen(true, true, true, true, true, true, true, true, true);
    }
}
