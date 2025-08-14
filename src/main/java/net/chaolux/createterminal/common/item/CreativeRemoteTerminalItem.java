package net.chaolux.createterminal.common.item;

import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import io.netty.buffer.Unpooled;
import net.chaolux.createterminal.common.menu.RemoteStockKeeperMenu;
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
        if(!(be instanceof StockTickerBlockEntity)) return InteractionResult.PASS;
        ItemStack stack=ctx.getItemInHand();
        CompoundTag tag=getOrCreateCustomTag(stack);
        ListTag posList=tag.getList("terminals", Tag.TAG_LONG);
        ListTag dimList=tag.getList("dims",Tag.TAG_STRING);
        long newPosLong=pos.asLong();
        String newDim=level.dimension().location().toString();
        for(int i=0; i<posList.size(); i++) {
            long existing=((LongTag) posList.get(i)).getAsLong();
            String dimStr=dimList.getString(i);
            if(existing==newPosLong && dimStr.equals(newDim)) {
                ctx.getPlayer().displayClientMessage(Component.translatable("tooltip.createterminal.bound_existing"),true);
                return InteractionResult.FAIL;
            }
        }
        posList.add(LongTag.valueOf(newPosLong));
        dimList.add(StringTag.valueOf(newDim));
        tag.put("terminals",posList);
        tag.put("dims",dimList);
        setCustomTag(stack,tag);
        ctx.getPlayer().displayClientMessage(Component.translatable("tooltip.createterminal.bound_success",pos.toShortString()),true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(level.isClientSide) return InteractionResultHolder.pass(player.getItemInHand(hand));
        ItemStack stack=player.getItemInHand(hand);
        CompoundTag tag=getCustomTag(stack);
        if(tag==null || !tag.contains("terminals") || !tag.contains("dims")) {
            player.displayClientMessage(Component.translatable("tooltip.createterminal.not_bound"),true);
            return InteractionResultHolder.pass(stack);
        }
        ListTag posList=tag.getList("terminals",Tag.TAG_LONG);
        ListTag dimList=tag.getList("dims",Tag.TAG_STRING);
        BlockPos bestPos=null;
        double bestDist=Double.MAX_VALUE;
        for(int i=0; i<posList.size(); i++) {
            long raw=((LongTag) posList.get(i)).getAsLong();
            BlockPos pos=BlockPos.of(raw);
            String dimStr=dimList.getString(i);
            ResourceKey<Level> dimKey=ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dimStr));
            if(!level.dimension().equals(dimKey)) continue;
            if(!level.hasChunkAt(pos)) continue;
            double dist=player.blockPosition().distSqr(pos);
            if(dist<bestDist) {
                bestDist=dist;
                bestPos=pos;
            }
        }
        if(bestPos==null) {
            player.displayClientMessage(Component.translatable("tooltip.createterminal.lost"),true);
            return InteractionResultHolder.fail(stack);
        }
        MenuType<?> menuType= BuiltInRegistries.MENU.get(ResourceLocation.fromNamespaceAndPath("create","stock_keeper_request"));
        if(menuType==null) return InteractionResultHolder.fail(stack);
        BlockEntity blockEntity=level.getBlockEntity(bestPos);
        if(!(blockEntity instanceof StockTickerBlockEntity stockTickerBlockEntity)) {
            player.displayClientMessage(Component.translatable("tooltip.createterminal.lost"),true);
            return InteractionResultHolder.fail(stack);
        }
        MenuProvider provider=new SimpleMenuProvider((id, inv, ply)->new RemoteStockKeeperMenu(menuType,id,inv,stockTickerBlockEntity),Component.literal("Stock Keeper"));
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
        CompoundTag tag=getCustomTag(stack);
        if(tag!=null && tag.contains("terminals")) {
            ListTag posList=tag.getList("terminals",Tag.TAG_LONG);
            tooltip.add(Component.translatable("tooltip.createterminal.header").withStyle(ChatFormatting.GRAY));
            int show=0;
            boolean showAll= Screen.hasShiftDown();
            for(int i=0; i<posList.size(); i++) {
                if(show>=5 && !showAll) {
                    tooltip.add(styleBracket(Component.translatable("tooltip.createterminal.more",posList.size()-show).withStyle(ChatFormatting.DARK_GRAY)));
                    break;
                }
                long raw=((LongTag) posList.get(i)).getAsLong();
                BlockPos pos=BlockPos.of(raw);
                tooltip.add(Component.translatable("tooltip.createterminal.bound",pos.getX(),pos.getY(),pos.getZ()).withStyle(ChatFormatting.GRAY));
                show++;
            }
        } else {
            tooltip.add(Component.translatable("tooltip.createterminal.not_bound").withStyle(ChatFormatting.GRAY));
        }
    }

    private static CompoundTag getCustomTag(ItemStack stack) {
        if(!stack.has(DataComponents.CUSTOM_DATA)) return null;
        return stack.get(DataComponents.CUSTOM_DATA).copyTag();
    }

    private static CompoundTag getOrCreateCustomTag(ItemStack stack) {
        CompoundTag tag=getCustomTag(stack);
        if(tag == null) tag=new CompoundTag();
        if(!tag.contains("terminals",Tag.TAG_LIST)) tag.put("terminals",new ListTag());
        if(!tag.contains("dims",Tag.TAG_LIST)) tag.put("dims",new ListTag());
        return tag;
    }

    private static void setCustomTag(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}
