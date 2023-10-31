package mc.recraftors.dumpster.parsers.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.Objectable;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * A recipe type parser class interface.
 * Must be used alongside {@link TargetRecipeType}
 * and registered as {@code recipe-dump} in the
 * mod's entry-points in order to be effective.
 */
public interface RecipeJsonParser extends Objectable {
    /**
     * Puts in the specified recipe of theoretically matching type,
     * to be parsed as JSON with the {@link #toJson()} method.
     * <p>
     * Returns whether the provided recipe was accepted
     * @param recipe The recipe to take in.
     * @return Whether the provided recipe was accepted
     */
    InResult in(Recipe<?> recipe);

    /**
     * Parses <b>one</b> recipe to JSON and returns the resulting object
     * @return The last added recipe parsed to JSON
     */
    @Override
    JsonObject toJson();

    /**
     * Returns whether the current recipe type is special.
     * <p>
     * Special recipes are dumped in a special folder, since they
     * cannot be modified via datapacks.
     * @return Whether the current recipe type is special.
     */
    default boolean isSpecial() {
        return false;
    }

    /**
     * Runs after each dumping cycle.
     * Allows for parsers to clear statically stored values.
     */
    default void cycle() {}

    /**
     * When the inserted recipe builds up a new recipe, from which it
     * supposedly gets generated, with a different ID, this method
     * allows to provide that other ID at which the parsed JSON should
     * be stored.
     * <p>
     * Warning: make sure to return {@code null} if using it but doesn't
     * affect the current recipe. This meaning, as much as possible, return
     * it from the current recipe, or clear your custom attribute when
     * inserting a new recipe.
     * @return The potential alternative ID of the current recipe.
     */
    default Identifier alternativeId() {
        return null;
    }

    static JsonObject recipeOutput(Recipe<?> recipe) {
        JsonObject res = new JsonObject();
        res.add("item", new JsonPrimitive(Registry.ITEM.getId(recipe.getOutput().getItem()).toString()));
        if (recipe.getOutput().getCount() > 1) {
            res.add("count", new JsonPrimitive(recipe.getOutput().getCount()));
        }
        return res;
    }

    static void addGroup(JsonObject object, Recipe<?> recipe) {
        if (recipe.getGroup() != null && !recipe.getGroup().isEmpty()) {
            object.add("group", new JsonPrimitive(recipe.getGroup()));
        }
    }

    /**
     * The output options of using the {@link #in} method
     */
    enum InResult {
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
        IGNORED
    }
}
