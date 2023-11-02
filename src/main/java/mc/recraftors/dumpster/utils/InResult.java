package mc.recraftors.dumpster.utils;

import net.minecraft.util.Identifier;

/**
 * The output options of using parsers' #in method
 */
public enum InResult {
    /**
     * When the parser successfully took in the provided recipe
     */
    SUCCESS,
    /**
     * When the parser failed to take in the provided recipe
     */
    FAILURE,
    /**
     * When the provided recipe should be ignored (e.g. dynamic recipes based on a single one)
     */
    IGNORED;

    public record Result<T> (InResult result, Identifier id, Identifier type, T value, boolean isSpecial) {}
}
