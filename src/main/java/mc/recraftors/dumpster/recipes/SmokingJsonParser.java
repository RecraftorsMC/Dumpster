package mc.recraftors.dumpster.recipes;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.SmokingRecipe;

@TargetRecipeType(SmokingJsonParser.TYPE)
public final class SmokingJsonParser extends AbstractCookingJsonParser {
    public static final String TYPE = "minecraft:smoking";
    public SmokingJsonParser() {
        super(TYPE);
    }

    @Override
    public void in(Recipe<?> recipe) {
        if (recipe instanceof SmokingRecipe r) {
            take(r);
        }
    }
}