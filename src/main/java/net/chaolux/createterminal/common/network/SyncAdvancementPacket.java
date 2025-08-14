package net.chaolux.createterminal.common.network;

import io.netty.buffer.ByteBuf;
import net.chaolux.createterminal.client.ClientAdvancementCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public record SyncAdvancementPacket(boolean hasDragonKill) implements CustomPacketPayload {
    public static final Type<SyncAdvancementPacket> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("createterminal", "sync_advancement"));

    public static final StreamCodec<ByteBuf,SyncAdvancementPacket> STREAM_CODEC=StreamCodec.composite(ByteBufCodecs.BOOL,SyncAdvancementPacket::hasDragonKill,SyncAdvancementPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
