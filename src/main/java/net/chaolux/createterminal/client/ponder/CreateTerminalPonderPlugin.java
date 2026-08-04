package net.chaolux.createterminal.client.ponder;

import net.chaolux.createterminal.CreateTerminal;
import net.chaolux.createterminal.client.ponder.scene.TerminalPonderScenes;
import net.chaolux.createterminal.registry.item.ModItems;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class CreateTerminalPonderPlugin implements PonderPlugin {
    private static final ResourceLocation STOCK_TICKER=new ResourceLocation("create","high_logistics/stock_ticker");

    @Override
    public String getModId() {
        return CreateTerminal.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> ponderSceneRegistrationHelper) {
        PonderSceneRegistrationHelper<Item> itemPonderSceneRegistrationHelper=ponderSceneRegistrationHelper.withKeyFunction(ForgeRegistries.ITEMS::getKey);
        itemPonderSceneRegistrationHelper.addStoryBoard(ModItems.REMOTE_TERMINAL.get(),STOCK_TICKER, TerminalPonderScenes::remoteTerminal);
        itemPonderSceneRegistrationHelper.addStoryBoard(ModItems.ADVANCED_REMOTE_TERMINAL.get(),STOCK_TICKER,TerminalPonderScenes::advancedRemoteTerminal);
        itemPonderSceneRegistrationHelper.addStoryBoard(ModItems.CREATIVE_REMOTE_TERMINAL.get(),STOCK_TICKER,TerminalPonderScenes::advancedRemoteTerminal);
    }
}
