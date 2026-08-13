package net.chaolux.createterminal.data.recipe;

import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.chaolux.createterminal.registry.item.ModItems;
import net.chaolux.createterminal.registry.recipe.ModRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;

public class TerminalExpandMixingRecipe extends MixingRecipe {
    private static final ThreadLocal<BasinBlockEntity> BASIN=new ThreadLocal<>();
    public TerminalExpandMixingRecipe(ProcessingRecipeParams processingRecipeParams) {
        super(processingRecipeParams);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TERMINAL_EXPAND_MIXING_SERIALIZER.get();
    }

    @Override
    public List<ItemStack> rollResults() {
        BasinBlockEntity basinBlockEntity=BASIN.get();
        if(basinBlockEntity == null) return List.of();
        IItemHandler iItemHandler=basinBlockEntity.getLevel().getCapability(Capabilities.ItemHandler.BLOCK,basinBlockEntity.getBlockPos(),null);
        if(iItemHandler == null) return List.of();
        for (int index=0;index < iItemHandler.getSlots();index++) {
            ItemStack itemStack=iItemHandler.getStackInSlot(index);
            if(!itemStack.is(ModItems.ADVANCED_REMOTE_TERMINAL.get())) continue;
            ItemStack stack=AdvancedRemoteTerminalRecipe.expandTerminal(itemStack);
            return List.of(stack);
        }
        return List.of();
    }

    public static void setBasin(BasinBlockEntity basinBlockEntity) {
        BASIN.set(basinBlockEntity);
    }

    public static void clearBasin() {
        BASIN.remove();
    }
}
