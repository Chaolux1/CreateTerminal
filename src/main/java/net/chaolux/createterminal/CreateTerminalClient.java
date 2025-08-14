package net.chaolux.createterminal;

import net.chaolux.createterminal.client.ClientAdvancementCache;
import net.chaolux.createterminal.common.utility.StyleUtils;
import net.chaolux.createterminal.registry.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = CreateTerminal.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = CreateTerminal.MOD_ID, value = Dist.CLIENT)
public class CreateTerminalClient {
    public CreateTerminalClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(()-> {
            ItemProperties.register(ModItems.CREATIVE_REMOTE_TERMINAL.get(),ResourceLocation.fromNamespaceAndPath("createterminal","bound"),(stack, world, entity, seed)-> {
                CompoundTag tag=getCustomTag(stack);
                if(tag !=null && tag.contains("terminals")) {
                    return 1.0f;
                }
                return 0.0f;
            });

            ItemProperties.register(ModItems.ADVANCED_REMOTE_TERMINAL.get(),ResourceLocation.fromNamespaceAndPath("createterminal","styles"),(stack,world,entity,seed)-> {
                CompoundTag tag=getCustomTag(stack);
                if(tag == null) return 0f;
                boolean bound=tag.contains("terminals");

                if(!bound) return 0f;
                String style=tag.getString("style");
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

            ItemProperties.register(ModItems.REMOTE_TERMINAL.get(),ResourceLocation.fromNamespaceAndPath("createterminal","styles"),(stack,world,entity,seed)-> {
                CompoundTag tag=getCustomTag(stack);
                if(tag == null) return 0f;
                boolean bound=tag.contains("boundPos") && tag.contains("boundDim");
                if(!bound) return 0f;
                String style=tag.getString("style");
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

    private static CompoundTag getCustomTag(ItemStack stack) {
        if(stack == null) return null;
        if(!stack.has(DataComponents.CUSTOM_DATA)) return null;
        return stack.get(DataComponents.CUSTOM_DATA).copyTag();
    }
}
