package net.chaolux.createterminal;

import net.chaolux.createterminal.registry.item.ModItems;
import net.chaolux.createterminal.registry.network.ModNetwork;
import net.chaolux.createterminal.registry.recipe.ModDataComponents;
import net.chaolux.createterminal.registry.recipe.ModRecipes;
import net.chaolux.createterminal.registry.sound.ModSounds;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(CreateTerminal.MOD_ID)
public class CreateTerminal {
    public static final String MOD_ID = "createterminal";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateTerminal(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        ModDataComponents.COMPONENT.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        modEventBus.addListener(ModNetwork::register);
        ModRecipes.SERIALIZERS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.REMOTE_TERMINAL.get());
            event.accept(ModItems.ADVANCED_REMOTE_TERMINAL.get());
            event.accept(ModItems.CREATIVE_REMOTE_TERMINAL.get());
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.MEMORY_CORE.get());
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
