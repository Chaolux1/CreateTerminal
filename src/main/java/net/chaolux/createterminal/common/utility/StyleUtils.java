package net.chaolux.createterminal.common.utility;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;

public class StyleUtils {
    public static boolean isMod(String modid) {
        return ModList.get().isLoaded(modid);
    }

    public static boolean hasAdvancement(ServerPlayer player, ResourceLocation id) {
        var advancement=player.server.getAdvancements().getAdvancement(id);
        return advancement !=null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }
}
