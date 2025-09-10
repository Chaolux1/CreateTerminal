package net.chaolux.createterminal.registry.recipe;

import com.mojang.serialization.Codec;
import net.chaolux.createterminal.common.item.data.RemoteBinding;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENT;
    public static final Supplier<DataComponentType<Integer>> EXPAND;
    public static final Supplier<DataComponentType<String>> STYLE;
    public static final Supplier<DataComponentType<RemoteBinding>> REMOTE_BINDING;


    static {
        COMPONENT=DeferredRegister.create(Registries.DATA_COMPONENT_TYPE,"createterminal");
        EXPAND=COMPONENT.register("expand",() -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
        STYLE=COMPONENT.register("style", () -> DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());
        REMOTE_BINDING=COMPONENT.register("remote_binding", () -> DataComponentType.<RemoteBinding>builder().persistent(RemoteBinding.CODEC).networkSynchronized(RemoteBinding.STREAM_CODEC).build());
    }
}
