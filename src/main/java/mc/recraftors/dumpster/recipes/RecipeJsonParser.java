package mc.recraftors.dumpster.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.Objectable;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.registry.Registry;

public interface RecipeJsonParser extends Objectable {
    /**
     * Puts in the specified recipe of theoretically matching type,
     * to be parsed as JSON with the {@link #toJson()} method.
     * @param recipe The recipe to take in.
     */
    boolean in(Recipe<?> recipe);

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
        return true;
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
}
