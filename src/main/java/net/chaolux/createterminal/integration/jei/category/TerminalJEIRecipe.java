package net.chaolux.createterminal.integration.jei.category;

import jdk.dynalink.Operation;
import net.chaolux.createterminal.CreateTerminal;
import net.chaolux.createterminal.registry.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record TerminalJEIRecipe(ResourceLocation resourceLocation, Operation operation) {
    public static final TerminalJEIRecipe EXPAND=new TerminalJEIRecipe(new ResourceLocation(CreateTerminal.MOD_ID,"terminal_expand"),Operation.EXPAND);
    public static final TerminalJEIRecipe CLEAR=new TerminalJEIRecipe(new ResourceLocation(CreateTerminal.MOD_ID,"terminal_clear"),Operation.CLEAR);

    public static List<TerminalJEIRecipe> list() {
        return List.of(EXPAND,CLEAR);
    }

    public ItemStack createInput() {
        ItemStack stack=new ItemStack(ModItems.ADVANCED_REMOTE_TERMINAL.get());
        CompoundTag compoundTag=stack.getOrCreateTag();
        compoundTag.put("terminals",new ListTag());
        compoundTag.put("dims",new ListTag());
        compoundTag.putString("style","blaze");
        if(operation == Operation.CLEAR) compoundTag.putInt("expand",1);
        return stack;
    }

    public ItemStack createOutput(ItemStack itemStack) {
        if(operation == Operation.CLEAR) return new ItemStack(ModItems.ADVANCED_REMOTE_TERMINAL.get());
        ItemStack stack=itemStack.copy();
        stack.setCount(1);
        CompoundTag compoundTag=stack.getOrCreateTag();
        compoundTag.putInt("expand",compoundTag.getInt("expand") + 1);
        return stack;
    }

    public enum Operation {
        EXPAND,CLEAR
    }
}
