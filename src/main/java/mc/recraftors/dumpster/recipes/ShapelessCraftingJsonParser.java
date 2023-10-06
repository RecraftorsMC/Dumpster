package mc.recraftors.dumpster.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.registry.Registry;

@TargetRecipeType(ShapelessCraftingJsonParser.TYPE)
public final class ShapelessCraftingJsonParser implements RecipeJsonParser {
    public static final String TYPE = "minecraft:crafting_shapeless";
    private ShapelessRecipe recipe;

    @Override
    public void in(Recipe<?> recipe) {
        if (recipe instanceof ShapelessRecipe s) {
            this.recipe = s;
        }
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public JsonObject toJson() {
        if (this.recipe == null) {
            return null;
        }
        JsonObject main = new JsonObject();
        main.add("type", new JsonPrimitive(TYPE));
        RecipeJsonParser.addGroup(main, recipe);
        JsonArray array = new JsonArray();
        this.recipe.getIngredients().forEach(ingredient -> {
            if (ingredient == null || ingredient.isEmpty()) return;
            array.add(ingredient.toJson());
        });
        main.add("ingredients", array);
        JsonObject res = new JsonObject();
        main.add("result", res);
        res.add("item", new JsonPrimitive(Registry.ITEM.getId(recipe.getOutput().getItem()).toString()));
        if (recipe.getOutput().getCount() > 1) {
            res.add("count", new JsonPrimitive(recipe.getOutput().getCount()));
        }
        return main;
    }
}
