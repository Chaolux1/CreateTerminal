package net.chaolux.createterminal.registry.recipe;

import net.chaolux.createterminal.data.recipe.AdvancedRemoteTerminalRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS;

    public static final Supplier<RecipeSerializer<AdvancedRemoteTerminalRecipe>> TERMINAL_EXPAND_SERIALIZER;

    static {
        SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"createterminal");

        TERMINAL_EXPAND_SERIALIZER=SERIALIZERS.register("terminal_expand", () -> new SimpleCraftingRecipeSerializer<>(AdvancedRemoteTerminalRecipe::new));
    }
}
