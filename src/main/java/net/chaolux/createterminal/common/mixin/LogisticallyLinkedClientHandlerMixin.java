package net.chaolux.createterminal.common.mixin;

import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedClientHandler;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.chaolux.createterminal.Config;
import net.chaolux.createterminal.common.item.AdvancedRemoteTerminalItem;
import net.chaolux.createterminal.common.item.CreativeRemoteTerminalItem;
import net.chaolux.createterminal.common.item.RemoteTerminalItem;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.outliner.Outline;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;

@Mixin(value = LogisticallyLinkedClientHandler.class,remap = false)
public abstract class LogisticallyLinkedClientHandlerMixin {
    @Shadow(remap = false)
    private static UUID previouslyHeldFrequency;

    @Inject(method = "tick",at = @At("HEAD"),cancellable = true,remap = false)
    private static void createterminal$highlightTerminalNetwork(CallbackInfo callbackInfo) {
        if(!Config.isTerminalNetworkHighlight()) return;
        LocalPlayer localPlayer= Minecraft.getInstance().player;
        if(localPlayer == null) return;
        UUID uuid=createterminal$getHeldTerminalFrequency(localPlayer);
        if(uuid == null) return;
        previouslyHeldFrequency=uuid;
        createterminal$renderNetworkOutline(localPlayer,uuid);
        callbackInfo.cancel();
    }

    private static UUID createterminal$getHeldTerminalFrequency(LocalPlayer localPlayer) {
        ItemStack itemStack=localPlayer.getMainHandItem();
        Item item=itemStack.getItem();
        CompoundTag compoundTag=itemStack.getTag();
        if(compoundTag == null) return null;
        if(item instanceof RemoteTerminalItem) return createterminal$getRemoteTerminalFrequency(localPlayer,compoundTag);
        if(item instanceof AdvancedRemoteTerminalItem) return createterminal$getNearestTerminalFrequency(localPlayer,compoundTag,Config.getAdvancedRemoteTerminalRange());
        if(item instanceof CreativeRemoteTerminalItem) return createterminal$getNearestTerminalFrequency(localPlayer,compoundTag,Config.CREATIVE_TERMINAL_RANGE);
        return null;
    }

    private static UUID createterminal$getRemoteTerminalFrequency(LocalPlayer localPlayer,CompoundTag compoundTag) {
        if(!compoundTag.contains("boundPos") || !compoundTag.contains("boundDim")) return null;
        ResourceLocation resourceLocation=ResourceLocation.tryParse(compoundTag.getString("boundDim"));
        if(resourceLocation == null || !localPlayer.level().dimension().location().equals(resourceLocation)) return null;
        BlockPos blockPos=BlockPos.of(compoundTag.getLong("boundPos"));
        if(!createterminal$isWithRange(localPlayer,blockPos,Config.getRemoteTerminalRange())) return null;
        return createterminal$getStockTickerFrequency(localPlayer,blockPos);
    }

    private static UUID createterminal$getNearestTerminalFrequency(LocalPlayer localPlayer,CompoundTag compoundTag,int maxRange) {
        if(!compoundTag.contains("terminals") || !compoundTag.contains("dims")) return null;
        ListTag listTag=compoundTag.getList("terminals", Tag.TAG_LONG);
        ListTag tags=compoundTag.getList("dims",Tag.TAG_STRING);
        int count=Math.min(listTag.size(),tags.size());
        UUID uuid=null;
        double distance=Double.MAX_VALUE;
        for(int index=0;index < count;index++) {
            ResourceLocation resourceLocation=ResourceLocation.tryParse(tags.getString(index));
            if(resourceLocation == null || !localPlayer.level().dimension().location().equals(resourceLocation)) continue;
            long pos=((LongTag) listTag.get(index)).getAsLong();
            BlockPos blockPos=BlockPos.of(pos);
            if(!createterminal$isWithRange(localPlayer,blockPos,maxRange)) continue;
            UUID frequency=createterminal$getStockTickerFrequency(localPlayer,blockPos);
            if(frequency == null) continue;
            double dist=localPlayer.blockPosition().distSqr(blockPos);
            if(dist >= distance) continue;
            distance=dist;
            uuid=frequency;
        }
        return uuid;
    }

    private static UUID createterminal$getStockTickerFrequency(LocalPlayer localPlayer,BlockPos blockPos) {
        if(!localPlayer.level().hasChunkAt(blockPos)) return null;
        BlockEntity blockEntity=localPlayer.level().getBlockEntity(blockPos);
        if(!(blockEntity instanceof StockTickerBlockEntity stockTickerBlockEntity)) return null;
        return stockTickerBlockEntity.behaviour.freqId;
    }

    private static boolean createterminal$isWithRange(LocalPlayer localPlayer,BlockPos blockPos,int maxRange) {
        return maxRange == Integer.MAX_VALUE || blockPos.closerThan(localPlayer.blockPosition(),maxRange);
    }

    private static void createterminal$renderNetworkOutline(LocalPlayer localPlayer,UUID uuid) {
        for(LogisticallyLinkedBehaviour logisticallyLinkedBehaviour : LogisticallyLinkedBehaviour.getAllPresent(uuid,false,true)) {
            SmartBlockEntity smartBlockEntity= logisticallyLinkedBehaviour.blockEntity;
            if(smartBlockEntity == null || smartBlockEntity.isRemoved() || smartBlockEntity.isChunkUnloaded()) continue;
            BlockPos blockPos=smartBlockEntity.getBlockPos();
            if(!localPlayer.blockPosition().closerThan(blockPos,64)) continue;
            VoxelShape voxelShape=smartBlockEntity.getBlockState().getShape(localPlayer.level(),blockPos);
            if(voxelShape.isEmpty()) continue;
            List<AABB> aabbList=voxelShape.toAabbs();
            for(int index=0;index < aabbList.size();index++) {
                AABB aabb=aabbList.get(index);
                Outliner.getInstance().showAABB(Pair.of(logisticallyLinkedBehaviour,index),aabb.inflate(-1 / 128f).move(blockPos),2).lineWidth(1 / 32f).disableLineNormals().colored(AnimationTickHolder.getTicks() % 16 < 8 ? 0x708DAD : 0x90ADCD);
            }
        }
    }

}
