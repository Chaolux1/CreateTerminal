package net.chaolux.createterminal.registry.item;

import net.chaolux.createterminal.common.item.AdvancedRemoteTerminalItem;
import net.chaolux.createterminal.common.item.CreativeRemoteTerminalItem;
import net.chaolux.createterminal.common.item.RemoteTerminalItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS;
    public static final Supplier<Item> REMOTE_TERMINAL;
    public static final Supplier<Item> ADVANCED_REMOTE_TERMINAL;
    public static final Supplier<Item> CREATIVE_REMOTE_TERMINAL;
    public static final Supplier<Item> MEMORY_CORE;

    public static Supplier<Item> registerWithTab(String name, Supplier<Item> supplier) {
        return ITEMS.register(name, supplier);
    }

    public static Item.Properties basicItem() {
        return new Item.Properties();
    }

    static {
        ITEMS = DeferredRegister.create(Registries.ITEM, "createterminal");

        REMOTE_TERMINAL = registerWithTab("remote_terminal", () -> new RemoteTerminalItem(basicItem().stacksTo(1)));
        ADVANCED_REMOTE_TERMINAL = registerWithTab("advanced_remote_terminal", () -> new AdvancedRemoteTerminalItem(basicItem()));
        CREATIVE_REMOTE_TERMINAL = registerWithTab("creative_remote_terminal", () -> new CreativeRemoteTerminalItem(basicItem().rarity(Rarity.EPIC).stacksTo(1)));
        MEMORY_CORE = registerWithTab("memory_core", () -> new Item(basicItem()));
    }
}
