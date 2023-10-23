package mc.recraftors.dumpster.parsers.carvers;

import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.Objectable;
import net.minecraft.world.gen.carver.CarverConfig;

/**
 * A Carver Config parser class interface.
 * Must be used alongside {@link TargetCarverConfigType}
 * and registered as {@code carver-dump} in the
 * mod's entry-points in order to be effective.
 */
public interface CarverJsonParser extends Objectable {
    /**
     * Puts in the specified parser of theoretically matching type,
     * to be parsed as JSON with the {@link #toJson()} method.
     * <p>
     * Returns whether the provided carver was accepted.
     * @param carver The carver to take in.
     * @return Whether the provided carver was accepted.
     */
    boolean in(CarverConfig carver);

    /**
     * Parses <b>one</b> carver to JSON and returns the resulting object.
     * @return The last added carver, parsed to JSON.
     */
    @Override
    JsonObject toJson();
}
