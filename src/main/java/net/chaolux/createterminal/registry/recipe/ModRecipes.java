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
    public static final DeferredRegister<RecipeType<?>> TYPES;

    public static final Supplier<RecipeSerializer<AdvancedRemoteTerminalRecipe>> TERMINAL_EXPAND_SERIALIZER;
    public static final Supplier<RecipeType<AdvancedRemoteTerminalRecipe>> TERMINAL_EXPAND_TYPE;

    static {
        SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"createterminal");
        TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"createterminal");

        TERMINAL_EXPAND_SERIALIZER=SERIALIZERS.register("terminal_expand", () -> new SimpleCraftingRecipeSerializer<>(AdvancedRemoteTerminalRecipe::new));
        TERMINAL_EXPAND_TYPE=TYPES.register("terminal_expand", () -> new RecipeType<>() {
        });
    }
}
