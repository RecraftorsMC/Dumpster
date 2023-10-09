package mc.recraftors.dumpster.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.SmithingRecipeParamsAccessor;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.SmithingRecipe;

@TargetRecipeType(SmithingJsonParser.TYPE)
public final class SmithingJsonParser implements RecipeJsonParser {
    public static final String TYPE = "minecraft:smithing";
    private SmithingRecipe recipe;

    @Override
    public InResult in(Recipe<?> recipe) {
        if (recipe instanceof SmithingRecipe s) {
            this.recipe = s;
            return InResult.SUCCESS;
        }
        return InResult.FAILURE;
    }

    @Override
    public JsonObject toJson() {
        JsonObject main = new JsonObject();
        main.add("type", new JsonPrimitive(TYPE));
        RecipeJsonParser.addGroup(main, recipe);
        main.add("base", ((SmithingRecipeParamsAccessor)recipe).dumpster$getBase().toJson());
        main.add("addition", ((SmithingRecipeParamsAccessor)recipe).dumster$getAddition().toJson());
        main.add("result", RecipeJsonParser.recipeOutput(this.recipe));
        return main;
    }
}
