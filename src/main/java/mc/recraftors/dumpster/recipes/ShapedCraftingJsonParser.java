package mc.recraftors.dumpster.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.registry.Registry;

import java.util.Map;

@TargetRecipeType(value = ShapedCraftingJsonParser.TYPE, supports = "crafting")
public final class ShapedCraftingJsonParser implements RecipeJsonParser {
    public static final String TYPE = "minecraft:crafting_shaped";
    private ShapedRecipe recipe;

    @Override
    public InResult in(Recipe<?> recipe) {
        if (recipe instanceof ShapedRecipe r) {
            this.recipe = r;
            return InResult.SUCCESS;
        }
        return InResult.FAILURE;
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
        JsonArray pattern = new JsonArray();
        JsonObject keys = new JsonObject();
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            Ingredient in = recipe.getIngredients().get(i);
            if (in.isEmpty()) continue;
            if (keys.entrySet().stream().map(e -> e.getValue().getAsJsonObject()).anyMatch(e -> e.equals(in.toJson())))
                continue;
            keys.add(String.valueOf((char) i+61), in.toJson());
        }
        for (int i = 0; i < recipe.getHeight(); i++) {
            StringBuilder s = new StringBuilder();
            for (int j = 0; j < recipe.getWidth(); j++) {
                Ingredient in = recipe.getIngredients().get(recipe.getWidth() * i + j);
                if (in.isEmpty()) s.append(" ");
                else {
                    s.append(keys.entrySet().stream()
                            .filter(e -> e.getValue().getAsJsonObject().equals(in.toJson()))
                            .findFirst().map(Map.Entry::getKey).orElse(" "));
                }
            }
            pattern.add(s.toString());
        }
        main.add("pattern", pattern);
        main.add("key", keys);
        JsonObject res = new JsonObject();
        res.add("item", new JsonPrimitive(Registry.ITEM.getId(recipe.getOutput().getItem()).toString()));
        if (recipe.getOutput().getCount() > 1) {
            res.add("count", new JsonPrimitive(recipe.getOutput().getCount()));
        }
        main.add("result", res);
        return main;
    }
}
