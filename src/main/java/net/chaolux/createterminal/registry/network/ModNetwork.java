package net.chaolux.createterminal.registry.network;

import net.chaolux.createterminal.client.ClientAdvancementCache;
import net.chaolux.createterminal.common.network.SyncAdvancementPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Optional;

public class ModNetwork {
    private static final String PROTOCOL_V="1.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar=event.registrar(PROTOCOL_V);
        registrar.playToClient(SyncAdvancementPacket.TYPE,SyncAdvancementPacket.STREAM_CODEC,(packet, context) -> {
            if(FMLEnvironment.dist == Dist.CLIENT) {
            context.enqueueWork(() -> ClientAdvancementCache.setDragonKill(packet.hasDragonKill()));
            }
        });
    }

    public static void sendToClient(ServerPlayer player,SyncAdvancementPacket packet) {
        PacketDistributor.sendToPlayer(player,packet);
    }
}
