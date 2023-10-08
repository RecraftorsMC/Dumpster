package mc.recraftors.dumpster.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.accessors.CuttingRecipeInputAccessor;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.util.registry.Registry;

@TargetRecipeType(StoneCuttingJsonParser.TYPE)
public class StoneCuttingJsonParser implements RecipeJsonParser {
    public static final String TYPE = "minecraft:stonecutting";
    private StonecuttingRecipe recipe;

    @Override
    public boolean isSpecial() {
        return false;
    }

    @Override
    public boolean in(Recipe<?> recipe) {
        if (recipe instanceof StonecuttingRecipe r) {
            this.recipe = r;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        JsonObject main = new JsonObject();
        main.add("type", new JsonPrimitive(TYPE));
        RecipeJsonParser.addGroup(main, recipe);
        main.add("ingredient", ((CuttingRecipeInputAccessor)recipe).dumpster$getInput().toJson());
        main.add("result", new JsonPrimitive(Registry.ITEM.getId(recipe.getOutput().getItem()).toString()));
        main.add("count", new JsonPrimitive(recipe.getOutput().getCount()));
        return main;
    }
}
