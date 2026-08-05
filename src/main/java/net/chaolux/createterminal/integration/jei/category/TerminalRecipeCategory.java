package net.chaolux.createterminal.integration.jei.category;

import com.simibubi.create.compat.jei.category.animations.AnimatedMixer;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.utility.CreateLang;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.chaolux.createterminal.CreateTerminal;
import net.chaolux.createterminal.registry.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidType;


public class TerminalRecipeCategory implements IRecipeCategory<TerminalJEIRecipe> {
    public static final RecipeType<TerminalJEIRecipe> RECIPE_TYPE=RecipeType.create(CreateTerminal.MOD_ID,"terminal_mixing", TerminalJEIRecipe.class);
    private static final int WIDTH=177;
    private static final int HEIGHT=103;
    private final IDrawable icon;
    private final AnimatedMixer mixer;

    public TerminalRecipeCategory(IGuiHelper iGuiHelper) {
        this.icon=iGuiHelper.createDrawableItemStack(new ItemStack(ModItems.ADVANCED_REMOTE_TERMINAL.get()));
        this.mixer=new AnimatedMixer();
    }

    @Override
    public RecipeType<TerminalJEIRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.createterminal.terminal_modification");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, TerminalJEIRecipe terminalJEIRecipe, IFocusGroup iFocusGroup) {
        ItemStack input=getFocuseTerminal(iFocusGroup,terminalJEIRecipe.createInput());
        ItemStack output=terminalJEIRecipe.createOutput(input);
        iRecipeLayoutBuilder.addInputSlot(17,51).setStandardSlotBackground().addItemStack(input).addRichTooltipCallback(((iRecipeSlotView, iTooltipBuilder) -> {
            if(terminalJEIRecipe.operation() == TerminalJEIRecipe.Operation.EXPAND) {
                iTooltipBuilder.add(Component.translatable("jei.createterminal.expand.input").withStyle(ChatFormatting.GRAY));
            } else {
                iTooltipBuilder.add(Component.translatable("jei.createterminal.clear.input").withStyle(ChatFormatting.GRAY));
            }
        }));
        if(terminalJEIRecipe.operation() == TerminalJEIRecipe.Operation.EXPAND) {
            iRecipeLayoutBuilder.addInputSlot(36,51).setStandardSlotBackground().addItemStack(new ItemStack(ModItems.MEMORY_CORE.get())).addRichTooltipCallback(((iRecipeSlotView, iTooltipBuilder) -> iTooltipBuilder.add(Component.translatable("jei.createterminal.expand.core").withStyle(ChatFormatting.GOLD))));
        } else {
            iRecipeLayoutBuilder.addInputSlot(36,51).setStandardSlotBackground().setFluidRenderer(FluidType.BUCKET_VOLUME,true,16,16).addFluidStack(Fluids.WATER,FluidType.BUCKET_VOLUME).addRichTooltipCallback(((iRecipeSlotView, iTooltipBuilder) -> iTooltipBuilder.add(Component.translatable("jei.createterminal.clear.water").withStyle(ChatFormatting.AQUA))));
            iRecipeLayoutBuilder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStack(new ItemStack(Items.WATER_BUCKET));
        }
        iRecipeLayoutBuilder.addOutputSlot(142,51).setOutputSlotBackground().addItemStack(output).addRichTooltipCallback(((iRecipeSlotView, iTooltipBuilder) -> {
            if(terminalJEIRecipe.operation() == TerminalJEIRecipe.Operation.EXPAND) {
                int level=output.getOrCreateTag().getInt("expand");
                iTooltipBuilder.add(Component.translatable("jei.createterminal.expand.output").withStyle(ChatFormatting.GREEN));
                iTooltipBuilder.add(Component.translatable("jei.createterminal.expand.level",level,level * 10).withStyle(ChatFormatting.DARK_GREEN));
            } else {
                iTooltipBuilder.add(Component.translatable("jei.createterminal.clear.output").withStyle(ChatFormatting.YELLOW));
            }
        }));
    }

    @Override
    public void draw(TerminalJEIRecipe terminalJEIRecipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics guiGraphics,double x,double y) {
        AllGuiTextures.JEI_DOWN_ARROW.render(guiGraphics, 136, 32);
        AllGuiTextures.JEI_SHADOW.render(guiGraphics, 81, 68);
        mixer.draw(guiGraphics, WIDTH / 2 + 3, 34);
        AllGuiTextures.JEI_NO_HEAT_BAR.render(guiGraphics, 4, 80);
        HeatCondition heatCondition = HeatCondition.NONE;
        guiGraphics.drawString(Minecraft.getInstance().font, CreateLang.translateDirect(heatCondition.getTranslationKey()), 9, 86, heatCondition.getColor(), false);
    }

    @Override
    public ResourceLocation getRegistryName(TerminalJEIRecipe terminalJEIRecipe) {
        return terminalJEIRecipe.resourceLocation();
    }

    private static ItemStack getFocuseTerminal(IFocusGroup iFocusGroup,ItemStack itemStack) {
        return iFocusGroup.getItemStackFocuses().map(itemStackIFocus -> itemStackIFocus.getTypedValue().getIngredient()).filter(stack -> stack.is(ModItems.ADVANCED_REMOTE_TERMINAL.get())).findFirst().map(ItemStack::copy).orElseGet(itemStack::copy);
    }
}
