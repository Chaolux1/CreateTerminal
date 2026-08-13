package net.chaolux.createterminal.data.recipe;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.recipe.DummyCraftingContainer;
import net.chaolux.createterminal.common.item.AdvancedRemoteTerminalItem;
import net.chaolux.createterminal.common.item.MemoryCoreItem;
import net.chaolux.createterminal.registry.item.ModItems;
import net.chaolux.createterminal.registry.recipe.ModRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

import javax.annotation.Nonnull;


public class AdvancedRemoteTerminalRecipe implements CraftingRecipe {
    private final ResourceLocation id;
    private final CraftingBookCategory category;
    private final Ingredient terminal;
    private final Ingredient core;
    public AdvancedRemoteTerminalRecipe(ResourceLocation id, CraftingBookCategory category) {
        this.id=id;
        this.category=category;
        this.terminal=Ingredient.of(ModItems.ADVANCED_REMOTE_TERMINAL.get());
        this.core=Ingredient.of(ModItems.MEMORY_CORE.get());
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        int hasTerminal=0;
        int hasCore=0;
        for(int i=0; i < inv.getContainerSize(); i++) {
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
    public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
        ItemStack terminalItem=findTerminal(inv);
        if(terminalItem.isEmpty()) return ItemStack.EMPTY;
        return expandTerminal(terminalItem);
    }

    @Override
    public boolean canCraftInDimensions(int w,int h) {
        return w * h >=2;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
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
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer craftingContainer) {
        NonNullList<ItemStack> nonNullList=NonNullList.withSize(craftingContainer.getContainerSize(),ItemStack.EMPTY);
        if(!(craftingContainer instanceof DummyCraftingContainer)) return nonNullList;
        for(int index=0;index < craftingContainer.getContainerSize();index++) {
            ItemStack itemStack=craftingContainer.getItem(index);
            if(!terminal.test(itemStack)) continue;
            nonNullList.set(index,expandTerminal(itemStack));
            break;
        }
        return nonNullList;
    }

    private ItemStack findTerminal(CraftingContainer craftingContainer) {
        for(int index=0;index < craftingContainer.getContainerSize();index++) {
            ItemStack itemStack=craftingContainer.getItem(index);
            if(terminal.test(itemStack)) return itemStack;
        }
        return ItemStack.EMPTY;
    }

    private ItemStack expandTerminal(ItemStack itemStack) {
        ItemStack stack=itemStack.copy();
        stack.setCount(1);
        CompoundTag compoundTag=stack.getOrCreateTag();
        compoundTag.putInt("expand",compoundTag.getInt("expand") + 1);
        return stack;
    }
}
