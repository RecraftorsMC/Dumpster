package mc.recraftors.dumpster.mixins.accessor;

import mc.recraftors.dumpster.utils.accessors.CuttingRecipeAccessor;
import net.minecraft.recipe.CuttingRecipe;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CuttingRecipe.class)
public abstract class CuttingRecipeMixin implements CuttingRecipeAccessor {
    @Shadow @Final protected Ingredient input;

    @Override
    public Ingredient dumpster$getInput() {
        return input;
    }
}