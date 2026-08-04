package net.chaolux.createterminal.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.chaolux.createterminal.CreateTerminal;
import net.chaolux.createterminal.integration.jei.category.TerminalJEIRecipe;
import net.chaolux.createterminal.integration.jei.category.TerminalRecipeCategory;
import net.chaolux.createterminal.registry.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    private static final ResourceLocation ID=new ResourceLocation(CreateTerminal.MOD_ID,"jei_plugin");
    private static final ResourceLocation TERMINAL_EXPAND=new ResourceLocation(CreateTerminal.MOD_ID,"terminal_expand");
    private static final ResourceLocation TERMINAL_CLEAR=new ResourceLocation(CreateTerminal.MOD_ID,"mixing/advanced_remote_terminal");
    private static final ResourceLocation CREATE_MIXING=new ResourceLocation("create","mixing");
    private static final ResourceLocation CREATE_SHAPELESS=new ResourceLocation("create","automatic_shapeless");

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
        List<CraftingRecipe> craftingRecipeList=iRecipeManager.createRecipeLookup(RecipeTypes.CRAFTING).get().filter(craftingRecipe -> craftingRecipe.getId().equals(TERMINAL_EXPAND)).toList();
        iRecipeManager.hideRecipes(RecipeTypes.CRAFTING,craftingRecipeList);
        iRecipeManager.getRecipeType(CREATE_MIXING).ifPresent(recipeType -> hideRecipeById(iRecipeManager,recipeType,TERMINAL_CLEAR));
        iRecipeManager.getRecipeType(CREATE_SHAPELESS).ifPresent(recipeType -> hideRecipeById(iRecipeManager,recipeType,TERMINAL_EXPAND));
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    private static void hideRecipeById(IRecipeManager iRecipeManager, RecipeType recipeType, ResourceLocation resourceLocation) {
        List list=iRecipeManager.createRecipeLookup(recipeType).get().filter(value -> value instanceof Recipe<?> recipe && recipe.getId().equals(resourceLocation)).toList();
        iRecipeManager.hideRecipes(recipeType,list);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration iRecipeCatalystRegistration) {
        Block mixer= ForgeRegistries.BLOCKS.getValue(new ResourceLocation("create","mechanical_mixer"));
        Block basin=ForgeRegistries.BLOCKS.getValue(new ResourceLocation("create","basin"));
        if(mixer != null) iRecipeCatalystRegistration.addRecipeCatalyst(new ItemStack(mixer), TerminalRecipeCategory.RECIPE_TYPE);
        if(basin != null) iRecipeCatalystRegistration.addRecipeCatalyst(new ItemStack(basin), TerminalRecipeCategory.RECIPE_TYPE);
    }
}
