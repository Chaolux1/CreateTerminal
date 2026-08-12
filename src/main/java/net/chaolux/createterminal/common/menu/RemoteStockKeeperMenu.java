package net.chaolux.createterminal.common.menu;

import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestMenu;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class RemoteStockKeeperMenu extends StockKeeperRequestMenu {
    public static final int RANGE=20;
    private final int maxRange;

    public RemoteStockKeeperMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
        this.maxRange=RANGE;
    }

    public RemoteStockKeeperMenu(MenuType<?> type, int id, Inventory inv, StockTickerBlockEntity contentHolder) {
        this(type, id, inv, contentHolder,RANGE);
    }

    public RemoteStockKeeperMenu(MenuType<?> type, int id, Inventory inv, StockTickerBlockEntity contentHolder,int maxRange) {
        super(type, id, inv, contentHolder);
        this.maxRange=Math.max(1,maxRange);
    }

    public int getMaxRange() {
        return maxRange;
    }

    public boolean isPos(BlockPos blockPos) {
        return contentHolder != null && contentHolder.getBlockPos().equals(blockPos);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}