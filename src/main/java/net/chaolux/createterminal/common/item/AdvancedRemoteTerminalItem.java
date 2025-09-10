package net.chaolux.createterminal.common.item;

import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import io.netty.buffer.Unpooled;
import net.chaolux.createterminal.common.item.data.RemoteBinding;
import net.chaolux.createterminal.common.menu.RemoteStockKeeperMenu;
import net.chaolux.createterminal.common.network.SyncAdvancementPacket;
import net.chaolux.createterminal.registry.network.ModNetwork;
import net.chaolux.createterminal.registry.recipe.ModDataComponents;
import net.chaolux.createterminal.registry.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.chaolux.createterminal.common.utility.StyleUtils.hasAdvancement;
import static net.chaolux.createterminal.common.utility.StyleUtils.styleBracket;

public class AdvancedRemoteTerminalItem extends Item {
    public AdvancedRemoteTerminalItem(Properties p_41383_) {
        super(p_41383_.stacksTo(1));
    }

    private static final int MAX_TERMINALS=25;
    private static int getMaxTerminals(ItemStack stack) {
        int expand=stack.getOrDefault(ModDataComponents.EXPAND.get(),0);
        return MAX_TERMINALS + expand * 10;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level=ctx.getLevel();
        BlockPos pos=ctx.getClickedPos();
        BlockEntity be=level.getBlockEntity(pos);
        if(!(be instanceof StockTickerBlockEntity)) return InteractionResult.PASS;
        ItemStack stack=ctx.getItemInHand();
        String style=stack.getOrDefault(ModDataComponents.STYLE.get(),"");
        if(style.isEmpty()) {
            stack.set(ModDataComponents.STYLE.get(),"blaze");
        }
        RemoteBinding binding=stack.getOrDefault(ModDataComponents.REMOTE_BINDING.get(),RemoteBinding.EMPTY);
        String dim=level.dimension().location().toString();
        if(binding.contain(pos,dim)) {
            Player player=ctx.getPlayer();
            if(player !=null) {
                player.displayClientMessage(Component.translatable("tooltip.createterminal.bound_existing"),true);
            }
            return InteractionResult.FAIL;
        }
        if(binding.size() >= getMaxTerminals(stack)) {
            level.playSound(null,pos,ModSounds.TERMINAL_LOST.get(), SoundSource.PLAYERS,1.0f,1.0f);
            Player player=ctx.getPlayer();
            if(player !=null) {
                player.displayClientMessage(Component.translatable("tooltip.createterminal.limit"),true);
            }
            return InteractionResult.FAIL;
        }
        stack.set(ModDataComponents.REMOTE_BINDING.get(),binding.add(pos,dim));
        if(!level.isClientSide) {
            level.playSound(null,pos,ModSounds.TERMINAL_ON.get(), SoundSource.PLAYERS,1.0f,1.0f);
            Player player=ctx.getPlayer();
            if(player !=null) {
                player.getInventory().setChanged();
                if(player.containerMenu !=null) {
                    player.containerMenu.broadcastChanges();
                }
                player.displayClientMessage(Component.translatable("tooltip.createterminal.bound_success",pos.toShortString()),true);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(level.isClientSide) return InteractionResultHolder.pass(player.getItemInHand(hand));
        ItemStack stack=player.getItemInHand(hand);
        RemoteBinding binding=stack.getOrDefault(ModDataComponents.REMOTE_BINDING.get(),RemoteBinding.EMPTY);
        if(binding.size() == 0) {
            player.displayClientMessage(Component.translatable("tooltip.createterminal.not_bound"),true);
            return InteractionResultHolder.pass(stack);
        }
        List<BlockPos> validPos=new ArrayList<>();
        String currentDim=level.dimension().location().toString();
        for(int i=0;i < binding.dims().size(); i++) {
            if(currentDim.equals(binding.dims().get(i))) {
                validPos.add(BlockPos.of(binding.terminals().get(i)));
            }
        }
        validPos.sort(Comparator.comparingDouble(p -> player.blockPosition().distSqr(p)));
        BlockPos bestPos=null;
        for(BlockPos pos:validPos) {
            if(!level.hasChunkAt(pos)) continue;
            BlockEntity be=level.getBlockEntity(pos);
            if(!(be instanceof StockTickerBlockEntity)) continue;
            if(!level.getBlockState(pos).is(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("create","stock_ticker")))) {
                continue;
            }
            bestPos=pos;
            break;
        }
        if(bestPos == null) {
            player.level().playSound(null,player.blockPosition(), ModSounds.TERMINAL_LOST.get(), SoundSource.PLAYERS,1.0f,1.0f);
            player.displayClientMessage(Component.translatable("tooltip.createterminal.lost"),true);
            return InteractionResultHolder.fail(stack);
        }
        boolean unlock=hasAdvancement((ServerPlayer) player,ResourceLocation.fromNamespaceAndPath("minecraft","end/kill_dragon"));
        ModNetwork.sendToClient((ServerPlayer) player,new SyncAdvancementPacket(unlock));
        MenuType<?> menuType= BuiltInRegistries.MENU.get(ResourceLocation.fromNamespaceAndPath("create","stock_keeper_request"));
        if(menuType==null) return InteractionResultHolder.fail(stack);
        BlockEntity blockEntity=level.getBlockEntity(bestPos);
        if(!(blockEntity instanceof StockTickerBlockEntity stockTickerBlockEntity)) {
            player.displayClientMessage(Component.translatable("tooltip.createterminal.lost"),true);
            return InteractionResultHolder.fail(stack);
        }
        MenuProvider provider=new SimpleMenuProvider((id,inv,ply)->new RemoteStockKeeperMenu(menuType,id,inv,stockTickerBlockEntity),Component.literal("Stock Keeper"));
        BlockPos finalBestPos=bestPos;
        ((ServerPlayer) player).openMenu(provider,data-> {
            data.writeBoolean(false);
            data.writeBoolean(false);
            data.writeBlockPos(finalBestPos);
        });
        player.displayClientMessage(Component.translatable("tooltip.createterminal.connected",bestPos.toShortString()),true);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        RemoteBinding binding=stack.getOrDefault(ModDataComponents.REMOTE_BINDING.get(),RemoteBinding.EMPTY);
        if(binding.size() > 0) {
            tooltip.add(Component.translatable("tooltip.createterminal.header").withStyle(ChatFormatting.DARK_GRAY));
            boolean showAll= Screen.hasShiftDown();
            int show=0;
            for(int i=0; i < binding.size(); i++) {
                if(show>=5 && !showAll) {
                    tooltip.add(styleBracket(Component.translatable("tooltip.createterminal.more",binding.size()-show).withStyle(ChatFormatting.DARK_GRAY)));
                    break;
                }
                BlockPos pos=BlockPos.of(binding.terminals().get(i));
                tooltip.add(Component.translatable("tooltip.createterminal.bound",pos.getX(),pos.getY(),pos.getZ()).withStyle(ChatFormatting.GRAY));
                show++;
            }
        } else {
            tooltip.add(Component.translatable("tooltip.createterminal.not_bound").withStyle(ChatFormatting.DARK_GRAY));
        }
        boolean showExpand=Screen.hasAltDown();
        if(showExpand) {
            tooltip.add(Component.translatable("tooltip.createterminal.expand", getMaxTerminals(stack)).withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(styleBracket(Component.translatable("tooltip.createterminal.ctrl_more").withStyle(ChatFormatting.DARK_GRAY)));

        }
        tooltip.add(styleBracket(Component.translatable("tooltip.createterminal.style").withStyle(ChatFormatting.GRAY)));
    }

    public static void setStyle(ItemStack stack, String style) {
        stack.set(ModDataComponents.STYLE.get(),style);
    }
}