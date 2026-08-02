package net.chaolux.createterminal.common.mixin;

import com.simibubi.create.content.logistics.stockTicker.LogisticalStockRequestPacket;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderRequestPacket;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import com.simibubi.create.foundation.utility.AdventureUtil;
import net.chaolux.createterminal.common.menu.RemoteStockKeeperMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = BlockEntityConfigurationPacket.class,remap = false)
public abstract class BlockEntityConfigurationPacketMixin<BE extends SyncedBlockEntity> {
    @Shadow(remap = false)
    protected BlockPos pos;

    @Shadow(remap = false)
    protected abstract void applySettings(ServerPlayer serverPlayer,BE be);

    @Shadow(remap = false)
    protected abstract boolean causeUpdate();

    @Inject(method = "handle",at = @At("HEAD"),cancellable = true,remap = false)
    private void createterminal$handleRemoteTerminalPacket(NetworkEvent.Context context, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        Object object=this;
        boolean supportObject=object instanceof PackageOrderRequestPacket || object instanceof LogisticalStockRequestPacket;
        if(!supportObject) return;
        ServerPlayer serverPlayer=context.getSender();
        if(serverPlayer == null || !(serverPlayer.containerMenu instanceof RemoteStockKeeperMenu remoteStockKeeperMenu)) return;
        context.enqueueWork(() -> createterminal$handleRemotePacket(serverPlayer,remoteStockKeeperMenu));
        callbackInfoReturnable.setReturnValue(true);
    }

    @SuppressWarnings("unchecked")
    private void createterminal$handleRemotePacket(ServerPlayer serverPlayer,RemoteStockKeeperMenu remoteStockKeeperMenu) {
        if(serverPlayer.containerMenu != remoteStockKeeperMenu) return;
        if(serverPlayer.isSpectator() || AdventureUtil.isAdventure(serverPlayer)) return;
        Level level=serverPlayer.level();
        if(level == null || !level.isLoaded(pos)) return;
        if(!remoteStockKeeperMenu.isPos(pos)) return;
        int maxRange= remoteStockKeeperMenu.getMaxRange();
        if(maxRange != Integer.MAX_VALUE && !pos.closerThan(serverPlayer.blockPosition(),maxRange)) return;
        BlockEntity blockEntity=level.getBlockEntity(pos);
        if(!(blockEntity instanceof StockTickerBlockEntity stockTickerBlockEntity)) return;
        if(!stockTickerBlockEntity.behaviour.mayInteractMessage(serverPlayer)) {
            serverPlayer.closeContainer();
            return;
        }
        applySettings(serverPlayer,(BE) stockTickerBlockEntity);
        if(!causeUpdate()) return;
        stockTickerBlockEntity.sendData();
        stockTickerBlockEntity.setChanged();
    }

}
