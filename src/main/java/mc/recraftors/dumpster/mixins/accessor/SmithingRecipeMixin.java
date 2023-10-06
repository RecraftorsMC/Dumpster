package mc.recraftors.dumpster.mixins.accessor;

import mc.recraftors.dumpster.utils.accessors.SmithingRecipeAccessor;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SmithingRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SmithingRecipe.class)
public abstract class SmithingRecipeMixin implements SmithingRecipeAccessor {
    @Shadow @Final Ingredient base;

    @Shadow @Final Ingredient addition;

    @Override
    public Ingredient dumpster$getBase() {
        return base;
    }

    @Override
    public Ingredient dumster$getAddition() {
        return addition;
    }
}
