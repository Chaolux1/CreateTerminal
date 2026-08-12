package net.chaolux.createterminal.registry.sound;

import net.chaolux.createterminal.CreateTerminal;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS;
    public static final Supplier<SoundEvent> TERMINAL_LOST;
    public static final Supplier<SoundEvent> TERMINAL_ON;
    public static final Supplier<SoundEvent> MUSIC_DISC_TERMINAL_PROTOCOL;

    public static Supplier<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("createterminal",name)));
    }

    static {
        SOUND_EVENTS=DeferredRegister.create(Registries.SOUND_EVENT,"createterminal");
        TERMINAL_LOST=register("lost");
        TERMINAL_ON=register("on");
        MUSIC_DISC_TERMINAL_PROTOCOL=SOUND_EVENTS.register("music_disc_terminal_protocol",() -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(CreateTerminal.MOD_ID,"music_disc_terminal_protocol")));

    }

}
