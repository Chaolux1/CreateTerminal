package net.chaolux.createterminal.common.network;

import com.mojang.logging.LogUtils;
import net.chaolux.createterminal.common.client.ClientAdvancementCache;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

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
