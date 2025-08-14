package net.chaolux.createterminal.registry.client;

import net.chaolux.createterminal.client.ClientKeyBind;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;


@EventBusSubscriber(modid = "createterminal", value = Dist.CLIENT)
public class ModEvents {
    @SubscribeEvent
    static void onRegisterKeyMapping(RegisterKeyMappingsEvent event) {
        event.register(ClientKeyBind.OPEN_TERMINAL);
    }
}
