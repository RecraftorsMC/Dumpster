package mc.recraftors.dumpster.recipes;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.SmeltingRecipe;

@TargetRecipeType(SmeltingJsonParser.TYPE)
public final class SmeltingJsonParser extends AbstractCookingJsonParser {
    public static final String TYPE = "minecraft:smelting";
    public SmeltingJsonParser() {
        super(TYPE);
    }

    @Override
    public InResult in(Recipe<?> recipe) {
        if (recipe instanceof SmeltingRecipe s) {
            take(s);
            return InResult.SUCCESS;
        }
        return InResult.FAILURE;
    }
}
