package net.chaolux.createterminal;

import com.mojang.logging.LogUtils;
import net.chaolux.createterminal.client.ClientAdvancementCache;
import net.chaolux.createterminal.common.utility.StyleUtils;
import net.chaolux.createterminal.registry.item.ModItems;
import net.chaolux.createterminal.registry.network.ModNetwork;
import net.chaolux.createterminal.registry.recipe.ModRecipes;
import net.chaolux.createterminal.registry.sound.ModSounds;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(CreateTerminal.MOD_ID)
public class CreateTerminal
{
    public static final String MOD_ID = "createterminal";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CreateTerminal()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModNetwork.register();
        ModRecipes.SERIALIZERS.register(modEventBus);
        ModRecipes.TYPES.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.REMOTE_TERMINAL);
            event.accept(ModItems.ADVANCED_REMOTE_TERMINAL);
            event.accept(ModItems.CREATIVE_REMOTE_TERMINAL);
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.MEMORY_CORE);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            event.enqueueWork(()-> {
                ItemProperties.register(ModItems.ADVANCED_REMOTE_TERMINAL.get(),new ResourceLocation("bound"),(stack,world,entity,seed)-> {
                    if(stack.hasTag() && stack.getTag().contains("terminals"))
                        return 1.0f;
                    return 0.0f;
                });

                ItemProperties.register(ModItems.CREATIVE_REMOTE_TERMINAL.get(),new ResourceLocation("bound"),(stack,world,entity,seed)-> {
                    if(stack.hasTag() && stack.getTag().contains("terminals"))
                        return 1.0f;
                    return 0.0f;
                });

                ItemProperties.register(ModItems.REMOTE_TERMINAL.get(),new ResourceLocation("styles"),(stack,world,entity,seed)-> {
                    if(stack == null || !stack.hasTag()) return 0f;
                    boolean bound=stack.getTag().contains("boundPos") && stack.getTag().contains("boundDim");
                    if(!bound) return 0f;
                    String style=stack.getTag().getString("style");
                    if(style.equals("cardboard") && !StyleUtils.isMod("createcardboardthings")) return 1f;
                    if(style.equals("end") && !ClientAdvancementCache.hasDragonKill()) return 1f;
                    return switch (style) {
                        case "blaze"->1f;
                        case "parrot"->2f;
                        case "cat"->3f;
                        case "cardboard"->4f;
                        case "end"->5f;
                        default -> 0f;
                    };
                });
            });
        }
    }
}
