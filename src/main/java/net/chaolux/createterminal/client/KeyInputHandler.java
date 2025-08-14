package net.chaolux.createterminal.client;

import net.chaolux.createterminal.common.item.AdvancedRemoteTerminalItem;
import net.chaolux.createterminal.common.item.RemoteTerminalItem;
import net.chaolux.createterminal.registry.recipe.ModDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import static net.chaolux.createterminal.common.utility.StyleUtils.isMod;

@EventBusSubscriber(modid = "createterminal", value = Dist.CLIENT)
public class KeyInputHandler {
    private static final String[] STYLES={
            "blaze","parrot","cat","cardboard","end"
    };
    private static boolean wasPress=false;
    @SubscribeEvent
    public static void onClientTick(LevelTickEvent.Post event) {
        if(!(event.getLevel() instanceof ClientLevel)) return;
        Player player=Minecraft.getInstance().player;
        if(player == null) return;
        if(Screen.hasControlDown() && Screen.hasShiftDown()) {
            if(!wasPress) {
                wasPress=true;
                ItemStack stack=player.getMainHandItem();
                Item item=stack.getItem();
                if(!(item instanceof RemoteTerminalItem || item instanceof AdvancedRemoteTerminalItem)) return;
                String current=stack.getOrDefault(ModDataComponents.STYLE.get(),"");
                int index=0;
                for(int i=0; i < STYLES.length; i++) {
                    if(STYLES[i].equals(current)) {
                        index=i;
                        break;
                    }
                }
                for(int attempt=0; attempt < STYLES.length; attempt++) {
                    index=(index+1) % STYLES.length;
                    String condition=STYLES[index];
                    if(condition.equals("cardboard") && !isMod("createcardboardthings")) continue;
                    if(condition.equals("end") && !ClientAdvancementCache.hasDragonKill()) continue;
                    if(item instanceof AdvancedRemoteTerminalItem) {
                        AdvancedRemoteTerminalItem.setStyle(stack,condition);
                    } else if(item instanceof RemoteTerminalItem) {
                        RemoteTerminalItem.setStyle(stack,condition);
                    }
                    player.setItemInHand(InteractionHand.MAIN_HAND,stack.copy());
                    player.displayClientMessage(Component.translatable("style.createterminal.header",Component.translatable("style.createterminal." + condition)),true);
                    break;
                }
            }
        } else {
            wasPress=false;
        }
        if(ClientKeyBind.OPEN_TERMINAL.consumeClick()) {
            if(player == null) return;
            int selectSlot=player.getInventory().selected;
            ItemStack stack=player.getInventory().getItem(8);
            if(!(stack.getItem() instanceof RemoteTerminalItem || stack.getItem() instanceof AdvancedRemoteTerminalItem)) return;
            player.getInventory().selected=8;
            if(player !=null)
                Minecraft.getInstance().gameMode.useItem(Minecraft.getInstance().player,InteractionHand.MAIN_HAND);
            player.getInventory().selected=selectSlot;

        }
    }
}
