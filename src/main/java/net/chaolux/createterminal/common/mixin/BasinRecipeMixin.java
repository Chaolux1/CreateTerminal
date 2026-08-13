package net.chaolux.createterminal.common.mixin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import net.chaolux.createterminal.data.recipe.TerminalExpandMixingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BasinRecipe.class,remap = false)
public abstract class BasinRecipeMixin {
    @Inject(method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z",at = @At("HEAD"),remap = false)
    private static void createterminal$setBasin(BasinBlockEntity basinBlockEntity, Recipe<?> recipe, boolean value, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if(recipe instanceof TerminalExpandMixingRecipe) TerminalExpandMixingRecipe.setBasin(basinBlockEntity);
    }

    @Inject(method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z",at = @At("RETURN"),remap = false)
    private static void createterminal$clearBasin(BasinBlockEntity basinBlockEntity, Recipe<?> recipe, boolean value, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if(recipe instanceof TerminalExpandMixingRecipe) TerminalExpandMixingRecipe.clearBasin();
    }
}
