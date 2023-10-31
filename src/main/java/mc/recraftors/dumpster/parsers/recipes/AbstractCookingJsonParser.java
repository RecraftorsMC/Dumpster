package mc.recraftors.dumpster.parsers.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.util.registry.Registry;

public abstract class AbstractCookingJsonParser implements RecipeJsonParser {
    protected final String type;
    private AbstractCookingRecipe recipe;

    protected AbstractCookingJsonParser(String type) {
        this.type = type;
    }

    protected final void take(AbstractCookingRecipe recipe) {
        this.recipe = recipe;
    }

    public AbstractCookingRecipe getRecipe() {
        return recipe;
    }

    @Override
    public JsonObject toJson() {
        if (this.recipe == null) {
            return null;
        }
        JsonObject main = new JsonObject();
        main.add("type", new JsonPrimitive(this.type));
        RecipeJsonParser.addGroup(main, recipe);
        main.add("ingredient", recipe.getIngredients().get(0).toJson());
        main.add("result", new JsonPrimitive(Registry.ITEM.getId(recipe.getOutput().getItem()).toString()));
        main.add("experience", new JsonPrimitive(recipe.getExperience()));
        main.add("cookingtime", new JsonPrimitive(recipe.getCookTime()));
        return main;
    }
}
