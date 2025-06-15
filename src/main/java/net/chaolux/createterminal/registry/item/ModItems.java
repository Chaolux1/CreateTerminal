package net.chaolux.createterminal.registry.item;

import net.chaolux.createterminal.common.item.AdvancedRemoteTerminalItem;
import net.chaolux.createterminal.common.item.RemoteTerminalItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS;
    public static final RegistryObject<Item> REMOTE_TERMINAL;
    public static final RegistryObject<Item> ADVANCED_REMOTE_TERMINAL;

    public static RegistryObject<Item> registerWithTab(String name, Supplier<Item> supplier) {
        return ITEMS.register(name, supplier);
    }

    public static Item.Properties basicItem() {
        return new Item.Properties();
    }

    static {
        ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "createterminal");

        REMOTE_TERMINAL = registerWithTab("remote_terminal", () -> new RemoteTerminalItem(basicItem()));
        ADVANCED_REMOTE_TERMINAL = registerWithTab("advanced_remote_terminal", () -> new AdvancedRemoteTerminalItem(basicItem()));
    }
}
