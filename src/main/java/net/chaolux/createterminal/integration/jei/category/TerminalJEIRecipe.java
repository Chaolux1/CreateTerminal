package net.chaolux.createterminal.integration.jei.category;

import net.chaolux.createterminal.CreateTerminal;
import net.chaolux.createterminal.common.item.data.RemoteBinding;
import net.chaolux.createterminal.registry.item.ModItems;
import net.chaolux.createterminal.registry.recipe.ModDataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record TerminalJEIRecipe(ResourceLocation resourceLocation, Operation operation) {
    public static final TerminalJEIRecipe EXPAND=new TerminalJEIRecipe(ResourceLocation.fromNamespaceAndPath(CreateTerminal.MOD_ID,"terminal_expand"),Operation.EXPAND);
    public static final TerminalJEIRecipe CLEAR=new TerminalJEIRecipe(ResourceLocation.fromNamespaceAndPath(CreateTerminal.MOD_ID,"terminal_clear"),Operation.CLEAR);

    public static List<TerminalJEIRecipe> list() {
        return List.of(EXPAND,CLEAR);
    }

    public ItemStack createInput() {
        ItemStack stack=new ItemStack(ModItems.ADVANCED_REMOTE_TERMINAL.get());
        stack.set(ModDataComponents.REMOTE_BINDING.get(), RemoteBinding.EMPTY);
        stack.set(ModDataComponents.STYLE.get(),"blaze");
        if(operation == Operation.CLEAR) stack.set(ModDataComponents.EXPAND.get(),1);
        return stack;
    }

    public ItemStack createOutput(ItemStack itemStack) {
        if(operation == Operation.CLEAR) return new ItemStack(ModItems.ADVANCED_REMOTE_TERMINAL.get());
        ItemStack stack=itemStack.copy();
        stack.setCount(1);
        int expand=stack.getOrDefault(ModDataComponents.EXPAND.get(),0);
        stack.set(ModDataComponents.EXPAND.get(),expand + 1);
        return stack;
    }

    public enum Operation {
        EXPAND,CLEAR
    }
}
