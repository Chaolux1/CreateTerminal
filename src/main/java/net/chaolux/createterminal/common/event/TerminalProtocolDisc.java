package net.chaolux.createterminal.common.event;

import net.chaolux.createterminal.CreateTerminal;
import net.chaolux.createterminal.registry.item.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = "createterminal", bus = Bus.FORGE)
public class TerminalProtocolDisc {
    private static final String PROGRESS= CreateTerminal.MOD_ID + ":terminal_protocol_disc";
    private static final int BLAZE=0;
    private static final int PARROT=1;
    private static final int CAT=2;
    private static final int CARDBOARD=3;
    private static final int DRAGON=4;
    private TerminalProtocolDisc() {

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent livingDeathEvent) {
        if(livingDeathEvent.isCanceled()) return;
        LivingEntity livingEntity=livingDeathEvent.getEntity();
        if(livingEntity.level().isClientSide()) return;
        ServerPlayer serverPlayer=findPlayer(livingDeathEvent);
        if(serverPlayer == null) return;
        int progress=getProgress(serverPlayer);
        if(livingEntity instanceof Blaze) {
            orReset(serverPlayer,progress,BLAZE,PARROT);
            return;
        }
        if(livingEntity instanceof Parrot) {
            orReset(serverPlayer,progress,PARROT,CAT);
            return;
        }
        if(livingEntity instanceof Cat) {
            orReset(serverPlayer,progress,CAT,CARDBOARD);
            return;
        }
        if(livingEntity instanceof EnderDragon enderDragon) {
            if(progress == DRAGON) dropDisc(enderDragon);
                resetProgress(serverPlayer);
                return;
        }
        if(livingEntity instanceof Mob) resetProgress(serverPlayer);
    }

    public static void onPackageDestroyed(ServerPlayer serverPlayer) {
        int progress=getProgress(serverPlayer);
        if(progress == CARDBOARD) {
            setProgress(serverPlayer,DRAGON);
            return;
        }
        resetProgress(serverPlayer);
    }

    private static void orReset(ServerPlayer serverPlayer,int currentProgress,int progress,int nextProgress) {
        if(currentProgress == progress) {
            setProgress(serverPlayer,nextProgress);
        } else {
            resetProgress(serverPlayer);
        }
    }

    private static ServerPlayer findPlayer(LivingDeathEvent livingDeathEvent) {
        Entity entity=livingDeathEvent.getSource().getEntity();
        if(entity instanceof ServerPlayer serverPlayer) return serverPlayer;
        LivingEntity livingEntity=livingDeathEvent.getEntity().getKillCredit();
        if(livingEntity instanceof ServerPlayer serverPlayer) return serverPlayer;
        return null;
    }

    private static int getProgress(ServerPlayer serverPlayer) {
        return serverPlayer.getPersistentData().getInt(PROGRESS);
    }

    private static void setProgress(ServerPlayer serverPlayer,int progress) {
        serverPlayer.getPersistentData().putInt(PROGRESS,progress);
    }

    private static void resetProgress(ServerPlayer serverPlayer) {
        serverPlayer.getPersistentData().remove(PROGRESS);
    }

    private static void dropDisc(EnderDragon enderDragon) {
        if(!(enderDragon.level() instanceof ServerLevel serverPlayer)) return;
        ItemStack itemStack=new ItemStack(ModItems.MUSIC_DISC_TERMINAL_PROTOCOL.get());
        ItemEntity itemEntity=new ItemEntity(serverPlayer,enderDragon.getX(),enderDragon.getY() + 0.5D,enderDragon.getZ(),itemStack);
        itemEntity.setDefaultPickUpDelay();
        itemEntity.setDeltaMovement(0.0D,0.2D,0.0D);
        serverPlayer.addFreshEntity(itemEntity);
    }
}
