package net.chaolux.createterminal.registry.recipe;

import net.chaolux.createterminal.data.recipe.AdvancedRemoteTerminalRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS;
    public static final DeferredRegister<RecipeType<?>> TYPES;

    public static final RegistryObject<RecipeSerializer<AdvancedRemoteTerminalRecipe>> TERMINAL_EXPAND_SERIALIZER;
    public static final RegistryObject<RecipeType<AdvancedRemoteTerminalRecipe>> TERMINAL_EXPAND_TYPE;

    static {
        SERIALIZERS=DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS,"createterminal");
        TYPES=DeferredRegister.create(ForgeRegistries.RECIPE_TYPES,"createterminal");

        TERMINAL_EXPAND_SERIALIZER=SERIALIZERS.register("terminal_expand", () -> new SimpleCraftingRecipeSerializer<>(AdvancedRemoteTerminalRecipe::new));
        TERMINAL_EXPAND_TYPE=TYPES.register("terminal_expand", () -> new RecipeType<>() {
        });
    }
}
