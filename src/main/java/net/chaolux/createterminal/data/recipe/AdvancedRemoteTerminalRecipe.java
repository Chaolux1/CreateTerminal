package net.chaolux.createterminal.data.recipe;

import com.simibubi.create.foundation.recipe.DummyCraftingContainer;
import net.chaolux.createterminal.registry.item.ModItems;
import net.chaolux.createterminal.registry.recipe.ModDataComponents;
import net.chaolux.createterminal.registry.recipe.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;


public class AdvancedRemoteTerminalRecipe implements CraftingRecipe {
    private final CraftingBookCategory category;
    private final Ingredient terminal;
    private final Ingredient core;
    public AdvancedRemoteTerminalRecipe(CraftingBookCategory category) {
        this.category=category;
        this.terminal=Ingredient.of(ModItems.ADVANCED_REMOTE_TERMINAL.get());
        this.core=Ingredient.of(ModItems.MEMORY_CORE.get());
    }

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        int hasTerminal=0;
        int hasCore=0;
        for(int i=0; i < inv.size(); i++) {
            ItemStack stack=inv.getItem(i);
            if(stack.isEmpty()) continue;
            if(terminal.test(stack)) {
                hasTerminal++;
            } else if(core.test(stack)) {
                hasCore++;
            } else {
                return false;
            }
        }
        return hasTerminal == 1 && hasCore == 1;
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider provider) {
        ItemStack terminalItem=findTerminal(inv);
        if(terminalItem.isEmpty()) return ItemStack.EMPTY;
        return expandTerminal(terminalItem);
    }

    @Override
    public boolean canCraftInDimensions(int w,int h) {
        return w * h >=2;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TERMINAL_EXPAND_SERIALIZER.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients=NonNullList.create();
        ingredients.add(terminal);
        ingredients.add(core);
        return ingredients;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }


    @Override
    public CraftingBookCategory category() {
        return this.category;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput craftingInput) {
        return NonNullList.withSize(craftingInput.size(),ItemStack.EMPTY);
    }

    private ItemStack findTerminal(CraftingInput craftingInput) {
        for(int index=0;index < craftingInput.size();index++) {
            ItemStack itemStack=craftingInput.getItem(index);
            if(terminal.test(itemStack)) return itemStack;
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack expandTerminal(ItemStack itemStack) {
        ItemStack stack=itemStack.copy();
        stack.setCount(1);
        int current=stack.getOrDefault(ModDataComponents.EXPAND.get(),0);
        stack.set(ModDataComponents.EXPAND.get(),current + 1);
        return stack;
    }
}
