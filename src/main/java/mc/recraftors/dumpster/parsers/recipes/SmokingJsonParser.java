package mc.recraftors.dumpster.parsers.recipes;

import mc.recraftors.dumpster.utils.InResult;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.SmokingRecipe;

@TargetRecipeType(SmokingJsonParser.TYPE)
public class SmokingJsonParser extends AbstractCookingJsonParser {
    public static final String TYPE = "minecraft:smoking";
    public SmokingJsonParser() {
        super(TYPE);
    }

    @Override
    public InResult in(Recipe<?> recipe) {
        if (recipe instanceof SmokingRecipe r) {
            take(r);
            return InResult.SUCCESS;
        }
        return InResult.FAILURE;
    }
}
