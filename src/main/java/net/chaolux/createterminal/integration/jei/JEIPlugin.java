package net.chaolux.createterminal.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.chaolux.createterminal.CreateTerminal;
import net.chaolux.createterminal.integration.jei.category.TerminalJEIRecipe;
import net.chaolux.createterminal.integration.jei.category.TerminalRecipeCategory;
import net.chaolux.createterminal.registry.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    private static final ResourceLocation ID=ResourceLocation.fromNamespaceAndPath(CreateTerminal.MOD_ID,"jei_plugin");
    private static final ResourceLocation TERMINAL_EXPAND=ResourceLocation.fromNamespaceAndPath(CreateTerminal.MOD_ID,"terminal_expand_manual_only");
    private static final ResourceLocation TERMINAL_CLEAR=ResourceLocation.fromNamespaceAndPath(CreateTerminal.MOD_ID,"mixing/advanced_remote_terminal");
    private static final ResourceLocation CREATE_MIXING=ResourceLocation.fromNamespaceAndPath("create","mixing");
    private static final ResourceLocation CREATE_SHAPELESS=ResourceLocation.fromNamespaceAndPath("create","automatic_shapeless");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration iRecipeCategoryRegistration) {
        iRecipeCategoryRegistration.addRecipeCategories(new TerminalRecipeCategory(iRecipeCategoryRegistration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration iRecipeRegistration) {
        iRecipeRegistration.addRecipes(TerminalRecipeCategory.RECIPE_TYPE, TerminalJEIRecipe.list());
        iRecipeRegistration.addItemStackInfo(new ItemStack(ModItems.MUSIC_DISC_TERMINAL_PROTOCOL.get()), Component.translatable("jei.createterminal.music_disc_terminal_protocol.desc"),Component.translatable("jei.createterminal.music_disc_terminal_protocol.progress"),Component.translatable("jei.createterminal.music_disc_terminal_protocol.defeat"));
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime iJeiRuntime) {
        IRecipeManager iRecipeManager=iJeiRuntime.getRecipeManager();
        List<RecipeHolder<CraftingRecipe>> craftingRecipeList=iRecipeManager.createRecipeLookup(RecipeTypes.CRAFTING).get().filter(craftingRecipe -> craftingRecipe.id().equals(TERMINAL_EXPAND)).toList();
        iRecipeManager.hideRecipes(RecipeTypes.CRAFTING,craftingRecipeList);
        iRecipeManager.getRecipeType(CREATE_MIXING).ifPresent(recipeType -> hideRecipeById(iRecipeManager,recipeType,TERMINAL_CLEAR));
        iRecipeManager.getRecipeType(CREATE_SHAPELESS).ifPresent(recipeType -> hideRecipeById(iRecipeManager,recipeType,TERMINAL_EXPAND));
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    private static void hideRecipeById(IRecipeManager iRecipeManager, RecipeType recipeType, ResourceLocation resourceLocation) {
        IRecipeCategory iRecipeCategory= iRecipeManager.getRecipeCategory(recipeType);
        List list=iRecipeManager.createRecipeLookup(recipeType).get().filter(object -> resourceLocation.equals(iRecipeCategory.getRegistryName(object))).toList();
        iRecipeManager.hideRecipes(recipeType,list);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration iRecipeCatalystRegistration) {
        Block mixer= BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("create","mechanical_mixer"));
        Block basin=BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("create","basin"));
        iRecipeCatalystRegistration.addRecipeCatalyst(new ItemStack(mixer), TerminalRecipeCategory.RECIPE_TYPE);
        iRecipeCatalystRegistration.addRecipeCatalyst(new ItemStack(basin), TerminalRecipeCategory.RECIPE_TYPE);
    }
}
