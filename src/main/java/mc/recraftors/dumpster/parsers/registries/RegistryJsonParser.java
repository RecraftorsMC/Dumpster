package mc.recraftors.dumpster.parsers.registries;

import mc.recraftors.dumpster.utils.InResult;
import mc.recraftors.dumpster.utils.Objectable;

public interface RegistryJsonParser extends Objectable {
    /**
     * Puts in the specified object of theoretically matching type,
     * to be parsed as a JSON object usingthe {@link #toJson()} method.
     * <p>
     * Returns whether the provided object was accepted.
     * @param o The object to take in.
     * @return Whether the provided object was accepted.
     */
    InResult in(Object o);
}
