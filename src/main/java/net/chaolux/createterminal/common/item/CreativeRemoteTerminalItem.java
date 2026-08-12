package net.chaolux.createterminal.common.item;

import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import io.netty.buffer.Unpooled;
import net.chaolux.createterminal.Config;
import net.chaolux.createterminal.common.item.data.RemoteBinding;
import net.chaolux.createterminal.common.menu.RemoteStockKeeperMenu;
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

import static net.chaolux.createterminal.common.utility.StyleUtils.styleBracket;

public class CreativeRemoteTerminalItem extends Item {
    public CreativeRemoteTerminalItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level=ctx.getLevel();
        BlockPos pos=ctx.getClickedPos();
        BlockEntity be=level.getBlockEntity(pos);
        Player player=ctx.getPlayer();
        if(!(be instanceof StockTickerBlockEntity stockTickerBlockEntity)) return InteractionResult.PASS;
        if(player == null) return InteractionResult.PASS;
        if(level.isClientSide) return InteractionResult.SUCCESS;
        if(!stockTickerBlockEntity.behaviour.mayInteractMessage(player)) return InteractionResult.FAIL;
        ItemStack stack=ctx.getItemInHand();
        String style=stack.getOrDefault(ModDataComponents.STYLE.get(),"");
        if(style.isEmpty()) {
            stack.set(ModDataComponents.STYLE.get(),"blaze");
        }
        RemoteBinding binding=stack.getOrDefault(ModDataComponents.REMOTE_BINDING.get(),RemoteBinding.EMPTY);
        String dim=level.dimension().location().toString();
        if(binding.contain(pos,dim)) {
            level.playSound(null,pos,ModSounds.TERMINAL_LOST.get(),SoundSource.PLAYERS,1.0f,1.0f);
            player.displayClientMessage(Component.translatable("tooltip.createterminal.bound_existing"),true);
            return InteractionResult.FAIL;
        }
        stack.set(ModDataComponents.REMOTE_BINDING.get(),binding.add(pos,dim));
            level.playSound(null,pos, ModSounds.TERMINAL_ON.get(), SoundSource.PLAYERS,1.0f,1.0f);
            player.displayClientMessage(Component.translatable("tooltip.createterminal.bound_success",pos.toShortString()),true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(level.isClientSide) return InteractionResultHolder.pass(player.getItemInHand(hand));
        ItemStack stack=player.getItemInHand(hand);
        RemoteBinding binding=stack.getOrDefault(ModDataComponents.REMOTE_BINDING.get(),RemoteBinding.EMPTY);
        if(binding.size() == 0) {
            player.level().playSound(null,player.blockPosition(),ModSounds.TERMINAL_LOST.get(),SoundSource.PLAYERS,1.0f,1.0f);
            player.displayClientMessage(Component.translatable("tooltip.createterminal.not_bound"),true);
            return InteractionResultHolder.pass(stack);
        }
        String currentDim=level.dimension().location().toString();
        List<BlockPos> candidates=new ArrayList<>();
        for(int i=0; i < binding.size(); i++) {
            if(currentDim.equals(binding.dims().get(i))) {
                candidates.add(BlockPos.of(binding.terminals().get(i)));
            }
        }
        candidates.sort(Comparator.comparingDouble(p -> player.blockPosition().distSqr(p)));
        StockTickerBlockEntity stockTickerBlock=null;
        BlockPos bestPos=null;
        for(BlockPos pos : candidates) {
            if(!level.hasChunkAt(pos)) continue;
            BlockEntity be=level.getBlockEntity(pos);
            if(!(be instanceof StockTickerBlockEntity stockTickerBlockEntity)) continue;
            if(!level.getBlockState(pos).is(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("create","stock_ticker")))) continue;
            if(!stockTickerBlockEntity.behaviour.mayInteractMessage(player)) return InteractionResultHolder.fail(stack);
            stockTickerBlock=stockTickerBlockEntity;
            bestPos=pos;
            break;
        }
        if(bestPos==null || stockTickerBlock == null) {
            player.level().playSound(null,player.blockPosition(),ModSounds.TERMINAL_LOST.get(),SoundSource.PLAYERS,1.0f,1.0f);
            player.displayClientMessage(Component.translatable("tooltip.createterminal.lost"),true);
            return InteractionResultHolder.fail(stack);
        }
        MenuType<?> menuType= BuiltInRegistries.MENU.get(ResourceLocation.fromNamespaceAndPath("create","stock_keeper_request"));
        if(menuType==null) return InteractionResultHolder.fail(stack);
        StockTickerBlockEntity stockTickerBlockEntity=stockTickerBlock;
        MenuProvider provider=new SimpleMenuProvider((id, inv, ply)->new RemoteStockKeeperMenu(menuType,id,inv,stockTickerBlockEntity, Config.CREATIVE_TERMINAL_RANGE),Component.literal("Stock Keeper"));
        BlockPos finalBestPos=bestPos;
        ((ServerPlayer) player).openMenu(provider, data-> {
            data.writeBoolean(false);
            data.writeBoolean(false);
            data.writeBlockPos(finalBestPos);
        });
        player.displayClientMessage(Component.translatable("tooltip.createterminal.connected",bestPos.toShortString()),true);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.add(Component.translatable("tooltip.createterminal.creative").withStyle(ChatFormatting.DARK_PURPLE));
        RemoteBinding binding=stack.getOrDefault(ModDataComponents.REMOTE_BINDING.get(),RemoteBinding.EMPTY);
        if(binding.size() > 0) {
            tooltip.add(Component.translatable("tooltip.createterminal.header").withStyle(ChatFormatting.GRAY));
            int show=0;
            boolean showAll= Screen.hasShiftDown();
            for(int i=0; i<binding.size(); i++) {
                if(show>=5 && !showAll) {
                    tooltip.add(styleBracket(Component.translatable("tooltip.createterminal.more",binding.size()-show).withStyle(ChatFormatting.DARK_GRAY)));
                    break;
                }
                BlockPos pos=BlockPos.of(binding.terminals().get(i));
                tooltip.add(Component.translatable("tooltip.createterminal.bound",pos.getX(),pos.getY(),pos.getZ()).withStyle(ChatFormatting.GRAY));
                show++;
            }
        } else {
            tooltip.add(Component.translatable("tooltip.createterminal.not_bound").withStyle(ChatFormatting.GRAY));
        }
    }
}
