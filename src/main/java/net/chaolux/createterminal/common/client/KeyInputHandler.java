package net.chaolux.createterminal.common.client;

import net.chaolux.createterminal.common.item.RemoteTerminalItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.checkerframework.checker.units.qual.C;

import static net.chaolux.createterminal.common.utility.StyleUtils.hasAdvancement;
import static net.chaolux.createterminal.common.utility.StyleUtils.isMod;

@Mod.EventBusSubscriber(modid = "createterminal", value = Dist.CLIENT)
public class KeyInputHandler {
    private static final String[] STYLES={
            "blaze","parrot","cat","cardboard","end"
    };
    private static boolean wasPress=false;
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase != TickEvent.Phase.END) return;
        Player player=Minecraft.getInstance().player;
        if(player == null) return;
        if(Screen.hasControlDown() && Screen.hasShiftDown()) {
            if(!wasPress) {
                wasPress=true;
                ItemStack stack=player.getMainHandItem();
                if(!(stack.getItem() instanceof RemoteTerminalItem)) return;
                CompoundTag tag=stack.getOrCreateTag();
                String current=tag.getString("style");
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
                    RemoteTerminalItem.setStyle(stack,condition);
                    player.setItemInHand(InteractionHand.MAIN_HAND,stack.copy());
                    player.displayClientMessage(Component.literal("Style: "+STYLES[index]),true);
                    break;
                }
            }
        } else {
            wasPress=false;
        }
    }
}
