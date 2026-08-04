package net.chaolux.createterminal.registry.sound;

import net.chaolux.createterminal.CreateTerminal;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS;
    public static final RegistryObject<SoundEvent> TERMINAL_LOST;
    public static final RegistryObject<SoundEvent> TERMINAL_ON;
    public static final RegistryObject<SoundEvent> MUSIC_DISC_TERMINAL_PROTOCOL;

    public static RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("createterminal",name)));
    }

    static {
        SOUND_EVENTS=DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,"createterminal");
        TERMINAL_LOST=register("lost");
        TERMINAL_ON=register("on");
        MUSIC_DISC_TERMINAL_PROTOCOL=SOUND_EVENTS.register("music_disc_terminal_protocol",() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CreateTerminal.MOD_ID,"music_disc_terminal_protocol")));
    }

}
