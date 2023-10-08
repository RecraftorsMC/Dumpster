package mc.recraftors.dumpster.recipes;

import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.Recipe;

@TargetRecipeType(CampfireCookingJsonParser.TYPE)
public final class CampfireCookingJsonParser extends AbstractCookingJsonParser {
    public static final String TYPE = "minecraft:campfire_cooking";

    public CampfireCookingJsonParser() {
        super(TYPE);
    }

    @Override
    public boolean in(Recipe<?> recipe) {
        if (recipe instanceof CampfireCookingRecipe s) {
            take(s);
            return true;
        }
        return false;
    }
}
