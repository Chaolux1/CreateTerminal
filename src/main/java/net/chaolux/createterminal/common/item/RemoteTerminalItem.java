package net.chaolux.createterminal.common.item;

import com.simibubi.create.AllMenuTypes;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestMenu;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import io.netty.buffer.Unpooled;
import net.chaolux.createterminal.Config;
import net.chaolux.createterminal.common.menu.RemoteStockKeeperMenu;
import net.chaolux.createterminal.common.network.SyncAdvancementPacket;
import net.chaolux.createterminal.registry.network.ModNetwork;
import net.chaolux.createterminal.registry.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

import static net.chaolux.createterminal.common.utility.StyleUtils.hasAdvancement;
import static net.chaolux.createterminal.common.utility.StyleUtils.styleBracket;


public class RemoteTerminalItem extends Item {
    public RemoteTerminalItem(Properties p_41383_) {
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
        CompoundTag tag=stack.getOrCreateTag();
        tag.putLong("boundPos",pos.asLong());
        tag.putString("boundDim",level.dimension().location().toString());
        if(!tag.contains("style")) {
            setStyle(stack,"blaze");
        }
            ctx.getPlayer().displayClientMessage(Component.translatable("tooltip.createterminal.bound",pos.getX(),pos.getY(),pos.getZ()),true);
            ctx.getLevel().playSound(null,pos,ModSounds.TERMINAL_ON.get(),SoundSource.PLAYERS,1.0f,1.0f);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(!level.isClientSide) {
            ItemStack stack=player.getItemInHand(hand);
            CompoundTag tag=stack.getTag();
            if(tag==null || !tag.contains("boundPos") || !tag.contains("boundDim")) {
                player.level().playSound(null,player.blockPosition(),ModSounds.TERMINAL_LOST.get(),SoundSource.PLAYERS,1.0f,1.0f);
                player.displayClientMessage(Component.translatable("tooltip.createterminal.not_bound"),true);
                return InteractionResultHolder.pass(stack);
            }
            BlockPos pos=BlockPos.of(tag.getLong("boundPos"));
            ResourceLocation dimId=ResourceLocation.tryParse(tag.getString("boundDim"));
            if(dimId == null) {
                player.displayClientMessage(Component.translatable("tooltip.createterminal.lost"),true);
                player.level().playSound(null,player.blockPosition(), ModSounds.TERMINAL_LOST.get(), SoundSource.PLAYERS,1.0f,1.0f);
                return InteractionResultHolder.fail(stack);
            }
            ResourceKey<Level> dimKey=ResourceKey.create(Registries.DIMENSION, dimId);
            if(!level.dimension().equals(dimKey)) {
                player.displayClientMessage(Component.translatable("tooltip.createterminal.lost"),true);
                player.level().playSound(null,player.blockPosition(), ModSounds.TERMINAL_LOST.get(), SoundSource.PLAYERS,1.0f,1.0f);
                return InteractionResultHolder.fail(stack);
            }
            int maxRange= Config.getRemoteTerminalRange();
            if(!isRange(player,pos,maxRange)) {
                player.displayClientMessage(Component.translatable("tooltip.createterminal.lost"),true);
                player.level().playSound(null,player.blockPosition(), ModSounds.TERMINAL_LOST.get(), SoundSource.PLAYERS,1.0f,1.0f);
                return InteractionResultHolder.fail(stack);
            }
            if(!level.hasChunkAt(pos)) {
                player.displayClientMessage(Component.translatable("tooltip.createterminal.lost"),true);
                player.level().playSound(null,player.blockPosition(), ModSounds.TERMINAL_LOST.get(), SoundSource.PLAYERS,1.0f,1.0f);
                return InteractionResultHolder.fail(stack);
            }
            BlockEntity blockEntity=level.getBlockEntity(pos);
            if(!(blockEntity instanceof StockTickerBlockEntity stock)) {
                player.displayClientMessage(Component.translatable("tooltip.createterminal.lost"),true);
                player.level().playSound(null,player.blockPosition(), ModSounds.TERMINAL_LOST.get(), SoundSource.PLAYERS,1.0f,1.0f);
                return InteractionResultHolder.fail(stack);
            }
            if(!stock.behaviour.mayInteractMessage(player)) return InteractionResultHolder.fail(stack);
            boolean unlock=hasAdvancement((ServerPlayer) player,new ResourceLocation("minecraft:end/kill_dragon"));
            ModNetwork.sendToClient((ServerPlayer) player,new SyncAdvancementPacket(unlock));
            MenuType<?> menuType=ForgeRegistries.MENU_TYPES.getValue(new ResourceLocation("create", "stock_keeper_request"));
            if(menuType==null) {
                return InteractionResultHolder.fail(stack);
            }
            MenuProvider provider=new SimpleMenuProvider((id,inv,ply)->new RemoteStockKeeperMenu(menuType, id,inv,stock,maxRange),Component.literal("Stock Keeper"));
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
        tooltip.add(styleBracket(Component.translatable("tooltip.createterminal.style").withStyle(ChatFormatting.GRAY)));
    }

    public static void setStyle(ItemStack stack, String style) {
        CompoundTag styles=stack.getOrCreateTag();
        styles.putString("style",style);
    }

    private static boolean isRange(Player player,BlockPos blockPos,int maxRange) {
        return maxRange == Integer.MAX_VALUE || blockPos.closerThan(player.blockPosition(),maxRange);
    }
}
