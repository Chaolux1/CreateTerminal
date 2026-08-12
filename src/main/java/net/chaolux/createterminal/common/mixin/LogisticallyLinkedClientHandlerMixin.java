package net.chaolux.createterminal.common.mixin;

import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedClientHandler;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.chaolux.createterminal.Config;
import net.chaolux.createterminal.common.item.AdvancedRemoteTerminalItem;
import net.chaolux.createterminal.common.item.CreativeRemoteTerminalItem;
import net.chaolux.createterminal.common.item.RemoteTerminalItem;
import net.chaolux.createterminal.common.item.data.RemoteBinding;
import net.chaolux.createterminal.registry.recipe.ModDataComponents;
import net.createmod.catnip.animation.AnimationTickHolder;
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
        if(item instanceof RemoteTerminalItem) return createterminal$getRemoteTerminalFrequency(localPlayer,itemStack);
        if(item instanceof AdvancedRemoteTerminalItem) return createterminal$getNearestTerminalFrequency(localPlayer,itemStack,Config.getAdvancedRemoteTerminalRange());
        if(item instanceof CreativeRemoteTerminalItem) return createterminal$getNearestTerminalFrequency(localPlayer,itemStack,Config.CREATIVE_TERMINAL_RANGE);
        return null;
    }

    private static UUID createterminal$getRemoteTerminalFrequency(LocalPlayer localPlayer,ItemStack itemStack) {
        CompoundTag compoundTag=RemoteTerminalItem.getCustomTag(itemStack);
        if(compoundTag == null || !compoundTag.contains("boundPos") || !compoundTag.contains("boundDim")) return null;
        ResourceLocation resourceLocation=ResourceLocation.tryParse(compoundTag.getString("boundDim"));
        if(resourceLocation == null || !localPlayer.level().dimension().location().equals(resourceLocation)) return null;
        BlockPos blockPos=BlockPos.of(compoundTag.getLong("boundPos"));
        if(!createterminal$isWithRange(localPlayer,blockPos,Config.getRemoteTerminalRange())) return null;
        return createterminal$getStockTickerFrequency(localPlayer,blockPos);
    }

    private static UUID createterminal$getNearestTerminalFrequency(LocalPlayer localPlayer,ItemStack itemStack,int maxRange) {
        RemoteBinding remoteBinding=itemStack.getOrDefault(ModDataComponents.REMOTE_BINDING.get(),RemoteBinding.EMPTY);
        UUID uuid=null;
        double distance=Double.MAX_VALUE;
        String string=localPlayer.level().dimension().location().toString();
        for(int index=0;index < remoteBinding.size();index++) {
            if(!string.equals(remoteBinding.dims().get(index))) continue;
            BlockPos blockPos=BlockPos.of(remoteBinding.terminals().get(index));
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
