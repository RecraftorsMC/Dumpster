package mc.recraftors.dumpster.recipes;

import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.Recipe;

@TargetRecipeType(BlastingJsonParser.TYPE)
public final class BlastingJsonParser extends AbstractCookingJsonParser {
    public static final String TYPE = "minecraft:blasting";

    public BlastingJsonParser() {
        super(TYPE);
    }

    @Override
    public InResult in(Recipe<?> recipe) {
        if (recipe instanceof BlastingRecipe r) {
            take(r);
            return InResult.SUCCESS;
        }
        return InResult.FAILURE;
    }
}
