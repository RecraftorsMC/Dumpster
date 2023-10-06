package mc.recraftors.dumpster.recipes;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.SmeltingRecipe;

@TargetRecipeType(CampfireCookingJsonParser.TYPE)
public final class CampfireCookingJsonParser extends AbstractCookingJsonParser {
    public static final String TYPE = "minecraft:campfire_cooking";

    public CampfireCookingJsonParser() {
        super(TYPE);
    }

    @Override
    public void in(Recipe<?> recipe) {
        if (recipe instanceof SmeltingRecipe s) {
            take(s);
        }
    }
}
