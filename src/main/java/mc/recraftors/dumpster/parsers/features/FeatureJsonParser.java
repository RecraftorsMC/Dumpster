package mc.recraftors.dumpster.parsers.features;


import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.Objectable;
import net.minecraft.world.gen.feature.FeatureConfig;

/**
 * A feature config parser class interface.
 * Must be used alongside {@link TargetFeatureConfigType}
 * and registered as {@code feature-dump} in the
 * mod's entry-points in order to be effective.
 */
public interface FeatureJsonParser extends Objectable {
    /**
     * Puts in the specified feature of theoretically matching type,
     * to be parsed as JSON with the {@link #toJson()} method.
     * <p>
     * Returns whether the provided feature was accepted.
     * @param feature The carver to take in.
     * @return Whether the provided feature was accepted.
     */
    boolean in(FeatureConfig feature);

    /**
     * Parses <b>one</b> feature to JSON and returns the resulting object.
     * @return The last added feature, parsed to JSON.
     */
    @Override
    JsonObject toJson();
}
