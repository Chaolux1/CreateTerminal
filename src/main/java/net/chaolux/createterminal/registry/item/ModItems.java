package net.chaolux.createterminal.registry.item;

import net.chaolux.createterminal.common.item.AdvancedRemoteTerminalItem;
import net.chaolux.createterminal.common.item.CreativeRemoteTerminalItem;
import net.chaolux.createterminal.common.item.MemoryCoreItem;
import net.chaolux.createterminal.common.item.RemoteTerminalItem;
import net.chaolux.createterminal.registry.sound.ModSounds;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS;
    public static final RegistryObject<Item> REMOTE_TERMINAL;
    public static final RegistryObject<Item> ADVANCED_REMOTE_TERMINAL;
    public static final RegistryObject<Item> CREATIVE_REMOTE_TERMINAL;
    public static final RegistryObject<Item> MEMORY_CORE;
    public static final RegistryObject<Item> MUSIC_DISC_TERMINAL_PROTOCOL;

    public static RegistryObject<Item> registerWithTab(String name, Supplier<Item> supplier) {
        return ITEMS.register(name, supplier);
    }

    public static Item.Properties basicItem() {
        return new Item.Properties();
    }

    static {
        ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "createterminal");

        REMOTE_TERMINAL = registerWithTab("remote_terminal", () -> new RemoteTerminalItem(basicItem().stacksTo(1)));
        ADVANCED_REMOTE_TERMINAL = registerWithTab("advanced_remote_terminal", () -> new AdvancedRemoteTerminalItem(basicItem().stacksTo(1)));
        CREATIVE_REMOTE_TERMINAL = registerWithTab("creative_remote_terminal", () -> new CreativeRemoteTerminalItem(basicItem().rarity(Rarity.EPIC).stacksTo(1)));
        MEMORY_CORE = registerWithTab("memory_core", () -> new MemoryCoreItem(basicItem()));
        MUSIC_DISC_TERMINAL_PROTOCOL = registerWithTab("music_disc_terminal_protocol",() -> new RecordItem(15, ModSounds.MUSIC_DISC_TERMINAL_PROTOCOL,new Item.Properties().stacksTo(1).rarity(Rarity.RARE),20 * 114));
    }
}
