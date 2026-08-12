package net.chaolux.createterminal;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final int CREATIVE_TERMINAL_RANGE=Integer.MAX_VALUE;
    public static final ModConfigSpec.IntValue REMOTE_TERMINAL_RANGE;
    public static final ModConfigSpec.IntValue ADVANCED_REMOTE_TERMINAL_RANGE;
    public static final ModConfigSpec.BooleanValue TERMINAL_NETWORK_HIGHLIGHT;
    static final ModConfigSpec SPEC;

    static {
        BUILDER.push("Experimental");
        BUILDER.comment("Remote terminal interaction range in blocks.","Large ranges work only while the chunk Stock Ticker chunk is load");
        REMOTE_TERMINAL_RANGE=BUILDER.comment("Maximum working range of a Remote Terminal.").defineInRange("remoteTerminalRange",20,1,30000000);
        ADVANCED_REMOTE_TERMINAL_RANGE=BUILDER.comment("Maximum working range of a Advanced Remote Terminal.").defineInRange("advancedRemoteTerminalRange",20,1,30000000);
        TERMINAL_NETWORK_HIGHLIGHT=BUILDER.comment("Show Create original logistics network outline while holding a bound terminal").define("showTerminalNetworkHighlight",true);
        BUILDER.pop();
        SPEC=BUILDER.build();
    }

    public static int getRemoteTerminalRange() {
        return REMOTE_TERMINAL_RANGE.get();
    }

    public static int getAdvancedRemoteTerminalRange() {
        return ADVANCED_REMOTE_TERMINAL_RANGE.get();
    }

    public static boolean isTerminalNetworkHighlight() {
        return TERMINAL_NETWORK_HIGHLIGHT.get();
    }
}
