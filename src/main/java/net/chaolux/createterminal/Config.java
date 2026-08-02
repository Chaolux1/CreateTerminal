package net.chaolux.createterminal;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = CreateTerminal.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final int CREATIVE_TERMINAL_RANGE=Integer.MAX_VALUE;
    public static final ForgeConfigSpec.IntValue REMOTE_TERMINAL_RANGE;
    public static final ForgeConfigSpec.IntValue ADVANCED_REMOTE_TERMINAL_RANGE;
    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER.push("Experimental");
        BUILDER.comment("Remote terminal interaction range in blocks.","Large ranges work only while the chunk Stock Ticker chunk is load");
        REMOTE_TERMINAL_RANGE=BUILDER.comment("Maximum working range of a Remote Terminal.").defineInRange("remoteTerminalRange",20,1,30000000);
        ADVANCED_REMOTE_TERMINAL_RANGE=BUILDER.comment("Maximum working range of a Advanced Remote Terminal.").defineInRange("advancedRemoteTerminalRange",20,1,30000000);
        BUILDER.pop();
        SPEC=BUILDER.build();
    }

    public static int getRemoteTerminalRange() {
        return REMOTE_TERMINAL_RANGE.get();
    }

    public static int getAdvancedRemoteTerminalRange() {
        return ADVANCED_REMOTE_TERMINAL_RANGE.get();
    }


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {

    }
}
