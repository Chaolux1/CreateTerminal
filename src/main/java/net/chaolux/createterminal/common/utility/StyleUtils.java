package net.chaolux.createterminal.common.utility;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;

public class StyleUtils {
    public static boolean isMod(String modid) {
        return ModList.get().isLoaded(modid);
    }

    public static boolean hasAdvancement(ServerPlayer player, ResourceLocation id) {
        var advancement=player.server.getAdvancements().get(id);
        return advancement !=null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    public static Component styleBracket(Component component) {
        String id=component.getString();
        Component result=Component.empty();
        int i=0;
        while(i < id.length()) {
            char c=id.charAt(i);
            if(c == '[') {
                result=result.copy().append(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY));
                i++;
                int start=i;
                while(i < id.length() && id.charAt(i) !=']') {
                    i++;
                }
                if(i > start) {
                    String inside=id.substring(start,i);
                    result=result.copy().append(Component.literal(inside).withStyle(ChatFormatting.GRAY));
                }
                if(i < id.length() && id.charAt(i) == '[') {
                    i++;
                }
            } else {
                int start=i;
                while(i < id.length() && id.charAt(i) !='[') {
                    i++;
                }
                String outside=id.substring(start,i);
                result=result.copy().append(Component.literal(outside).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
        return result;
    }
}
