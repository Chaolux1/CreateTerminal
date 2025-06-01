package net.chaolux.createterminal.common.item;

import com.simibubi.create.AllMenuTypes;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestMenu;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import io.netty.buffer.Unpooled;
import net.chaolux.createterminal.common.menu.RemoteStockKeeperMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;


public class RemoteTerminalItem extends Item {
    public RemoteTerminalItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level=ctx.getLevel();
        BlockPos pos=ctx.getClickedPos();
        BlockEntity be=level.getBlockEntity(pos);
        if(!(be instanceof StockTickerBlockEntity)) return InteractionResult.PASS;
        ItemStack stack=ctx.getItemInHand();
        CompoundTag tag=stack.getOrCreateTag();
        tag.putLong("boundPos",pos.asLong());
        tag.putString("boundDim",level.dimension().location().toString());
        if(!level.isClientSide) {
            ctx.getPlayer().displayClientMessage(Component.literal("Bound at " +pos),true);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(!level.isClientSide) {
            ItemStack stack=player.getItemInHand(hand);
            CompoundTag tag=stack.getTag();
            if(tag==null || !tag.contains("boundPos") || !tag.contains("boundDim")) {
                player.displayClientMessage(Component.literal("[debug] Not bound"),true);
                return InteractionResultHolder.pass(stack);
            }
            BlockPos pos=BlockPos.of(tag.getLong("boundPos"));
            String dimId=tag.getString("boundDim");
            ResourceKey<Level> dimKey=ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimId));
            player.displayClientMessage(Component.literal("[debug] boundPos:"+pos+",BoundDim:"+dimId),true);
            player.displayClientMessage(Component.literal("[debug] CurrentDim:"+level.dimension().location()),true);
            if(!level.dimension().equals(dimKey)) {
                player.displayClientMessage(Component.literal("[debug] Wrong dim"), true);
                return InteractionResultHolder.fail(stack);
            }
            if(!level.hasChunkAt(pos)) {
                player.displayClientMessage(Component.literal("[debug] Chunk pos not loded"),true);
            }
            MenuType<?> menuType=ForgeRegistries.MENU_TYPES.getValue(new ResourceLocation("create", "stock_keeper_request"));
            if(menuType==null) {
                player.displayClientMessage(Component.literal("[debug] MenuType not find"),true);
                return InteractionResultHolder.fail(stack);
            }
            FriendlyByteBuf menuBuf=new FriendlyByteBuf(Unpooled.buffer());
            menuBuf.writeBoolean(false);
            menuBuf.writeBoolean(false);
            menuBuf.writeBlockPos(pos);
            MenuProvider provider=new SimpleMenuProvider((id,inv,ply)->new RemoteStockKeeperMenu(menuType, id,inv,menuBuf),Component.literal("Stock Keeper"));
            player.displayClientMessage(Component.literal("[debug] Opening"),true);
            NetworkHooks.openScreen((ServerPlayer) player,provider,data-> {
                data.writeBoolean(false);
                data.writeBoolean(false);
                data.writeBlockPos(pos);
            });
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag=stack.getTag();
        if(tag !=null && tag.contains("boundPos") && tag.contains("boundDim")) {
            BlockPos pos=BlockPos.of(tag.getLong("boundPos"));
            tooltip.add(Component.translatable("tooltip.createterminal.bound",pos.getX(),pos.getY(),pos.getZ()).withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("tooltip.createterminal.not_bound").withStyle(ChatFormatting.GRAY));
        }
    }
}
