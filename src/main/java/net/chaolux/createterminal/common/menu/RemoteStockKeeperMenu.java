package net.chaolux.createterminal.common.menu;

import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestMenu;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class RemoteStockKeeperMenu extends StockKeeperRequestMenu {

    public RemoteStockKeeperMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public RemoteStockKeeperMenu(MenuType<?> type, int id, Inventory inv, StockTickerBlockEntity contentHolder) {
        super(type, id, inv, contentHolder);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}