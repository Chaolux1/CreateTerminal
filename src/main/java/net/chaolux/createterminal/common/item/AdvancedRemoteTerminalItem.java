package net.chaolux.createterminal.common.item;

import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import io.netty.buffer.Unpooled;
import net.chaolux.createterminal.Config;
import net.chaolux.createterminal.common.menu.RemoteStockKeeperMenu;
import net.chaolux.createterminal.common.network.SyncAdvancementPacket;
import net.chaolux.createterminal.registry.network.ModNetwork;
import net.chaolux.createterminal.registry.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.chaolux.createterminal.common.utility.StyleUtils.hasAdvancement;
import static net.chaolux.createterminal.common.utility.StyleUtils.styleBracket;

public class AdvancedRemoteTerminalItem extends Item {
    public AdvancedRemoteTerminalItem(Properties p_41383_) {
        super(p_41383_);
    }

    private static final int MAX_TERMINALS=25;
    private static int getMaxTerminals(ItemStack stack) {
        CompoundTag tag=stack.getOrCreateTag();
        return MAX_TERMINALS + tag.getInt("expand") * 10;
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
        ListTag posList=tag.getList("terminals",Tag.TAG_LONG);
        ListTag dimList=tag.getList("dims",Tag.TAG_STRING);
        if(!tag.contains("style")) {
            setStyle(stack,"blaze");
        }
        long newPosLong=pos.asLong();
        String newDim=level.dimension().location().toString();
        int intList=Math.min(posList.size(),dimList.size());
        for(int i=0; i<intList; i++) {
            long existing=((LongTag) posList.get(i)).getAsLong();
            String dimStr=dimList.getString(i);
            if(existing==newPosLong && dimStr.equals(newDim)) {
                ctx.getLevel().playSound(null,pos,ModSounds.TERMINAL_LOST.get(),SoundSource.PLAYERS,1.0f,1.0f);
                ctx.getPlayer().displayClientMessage(Component.translatable("tooltip.createterminal.bound_existing"),true);
                return InteractionResult.FAIL;
            }
        }
        if(posList.size() >= getMaxTerminals(stack)) {
            ctx.getLevel().playSound(null,pos,ModSounds.TERMINAL_LOST.get(),SoundSource.PLAYERS,1.0f,1.0f);
            ctx.getPlayer().displayClientMessage(Component.translatable("tooltip.createterminal.limit"),true);
            return InteractionResult.FAIL;
        }
        posList.add(LongTag.valueOf(newPosLong));
        dimList.add(StringTag.valueOf(newDim));
        tag.put("terminals",posList);
        tag.put("dims",dimList);
        ctx.getLevel().playSound(null,pos,ModSounds.TERMINAL_ON.get(),SoundSource.PLAYERS,1.0f,1.0f);
        ctx.getPlayer().displayClientMessage(Component.translatable("tooltip.createterminal.bound_success",pos.toShortString()),true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(level.isClientSide) return InteractionResultHolder.pass(player.getItemInHand(hand));
        ItemStack stack=player.getItemInHand(hand);
        CompoundTag tag=stack.getTag();
        if(tag==null || !tag.contains("terminals") || !tag.contains("dims")) {
            player.level().playSound(null,player.blockPosition(),ModSounds.TERMINAL_LOST.get(),SoundSource.PLAYERS,1.0f,1.0f);
            player.displayClientMessage(Component.translatable("tooltip.createterminal.not_bound"),true);
            return InteractionResultHolder.pass(stack);
        }
        ListTag posList=tag.getList("terminals",Tag.TAG_LONG);
        ListTag dimList=tag.getList("dims",Tag.TAG_STRING);
        int intList=Math.min(posList.size(),dimList.size());
        int maxRange= Config.getAdvancedRemoteTerminalRange();
        List<BlockPos> validPos=new ArrayList<>();
        for(int i=0; i < intList; i++) {
            long raw=((LongTag) posList.get(i)).getAsLong();
            BlockPos pos=BlockPos.of(raw);
            ResourceLocation dimStr=ResourceLocation.tryParse(dimList.getString(i));
            if(dimStr == null) continue;
            ResourceKey<Level> dimKey=ResourceKey.create(Registries.DIMENSION,dimStr);
            if(!level.dimension().equals(dimKey)) continue;
            if(!isRange(player,pos,maxRange)) continue;
            validPos.add(pos);
        }
        validPos.sort(Comparator.comparingDouble(p -> player.blockPosition().distSqr(p)));
        BlockPos bestPos=null;
        StockTickerBlockEntity bestStockTicker=null;
        ServerPlayer serverPlayer=(ServerPlayer) player;
        for(BlockPos pos:validPos) {
            if(!level.hasChunkAt(pos)) continue;
            BlockEntity be=level.getBlockEntity(pos);
            if(!(be instanceof StockTickerBlockEntity stockTickerBlockEntity)) continue;
            if(!level.getBlockState(pos).is(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("create","stock_ticker")))) continue;
            bestPos=pos;
            bestStockTicker=stockTickerBlockEntity;
            break;
        }
        if(bestPos == null || bestStockTicker == null) {
            player.level().playSound(null,player.blockPosition(), ModSounds.TERMINAL_LOST.get(), SoundSource.PLAYERS,1.0f,1.0f);
            player.displayClientMessage(Component.translatable("tooltip.createterminal.lost"),true);
            return InteractionResultHolder.fail(stack);
        }
        if(!bestStockTicker.behaviour.mayInteractMessage(player)) return InteractionResultHolder.fail(stack);
        boolean unlock=hasAdvancement((ServerPlayer) player,new ResourceLocation("minecraft:end/kill_dragon"));
        ModNetwork.sendToClient((ServerPlayer) player,new SyncAdvancementPacket(unlock));
        MenuType<?> menuType= ForgeRegistries.MENU_TYPES.getValue(new ResourceLocation("create","stock_keeper_request"));
        if(menuType==null) return InteractionResultHolder.fail(stack);
        final MenuType<?> finalMenuType=menuType;
        StockTickerBlockEntity stock=bestStockTicker;
        MenuProvider provider=new SimpleMenuProvider((id,inv,ply)->new RemoteStockKeeperMenu(finalMenuType,id,inv,stock,maxRange),Component.literal("Stock Keeper"));
        BlockPos finalBestPos=bestPos;
        NetworkHooks.openScreen((ServerPlayer) player,provider,data-> {
            data.writeBoolean(false);
            data.writeBoolean(false);
            data.writeBlockPos(finalBestPos);
        });
        player.displayClientMessage(Component.translatable("tooltip.createterminal.connected",bestPos.toShortString()),true);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag=stack.getTag();
        if(tag!=null && tag.contains("terminals")) {
            ListTag posList=tag.getList("terminals",Tag.TAG_LONG);
            tooltip.add(Component.translatable("tooltip.createterminal.header").withStyle(ChatFormatting.DARK_GRAY));
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
        CompoundTag styles=stack.getOrCreateTag();
        styles.putString("style",style);
    }

    private static boolean isRange(Player player,BlockPos blockPos,int maxRange) {
        return maxRange == Integer.MAX_VALUE || blockPos.closerThan(player.blockPosition(),maxRange);
    }
}
