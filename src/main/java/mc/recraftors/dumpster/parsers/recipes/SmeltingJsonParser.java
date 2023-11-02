package mc.recraftors.dumpster.parsers.recipes;

import mc.recraftors.dumpster.utils.InResult;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.SmeltingRecipe;

@TargetRecipeType(SmeltingJsonParser.TYPE)
public class SmeltingJsonParser extends AbstractCookingJsonParser {
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
