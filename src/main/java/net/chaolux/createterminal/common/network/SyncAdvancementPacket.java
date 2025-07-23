package net.chaolux.createterminal.common.network;

import net.chaolux.createterminal.client.ClientAdvancementCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncAdvancementPacket {
    private final boolean hasDragonKill;
    public SyncAdvancementPacket(boolean hasDragonKill) {
        this.hasDragonKill=hasDragonKill;
    }

    public SyncAdvancementPacket(FriendlyByteBuf buf) {
        this.hasDragonKill=buf.readBoolean();
    }

    public void toByte(FriendlyByteBuf buf) {
        buf.writeBoolean(hasDragonKill);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ClientAdvancementCache.setDragonKill(hasDragonKill);
        });
        context.get().setPacketHandled(true);
    }
}
