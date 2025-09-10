package net.chaolux.createterminal.common.item.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public record RemoteBinding(List<Long> terminals, List<String> dims) {
    public static final RemoteBinding EMPTY=new RemoteBinding(List.of(),List.of());
    public static final Codec<RemoteBinding> CODEC= RecordCodecBuilder.create(builder -> builder.group(Codec.list(Codec.LONG).fieldOf("terminals").forGetter(RemoteBinding::terminals),Codec.list(Codec.STRING).fieldOf("dims").forGetter(RemoteBinding::dims)).apply(builder,RemoteBinding::new));
    public static final StreamCodec<ByteBuf,RemoteBinding> STREAM_CODEC=StreamCodec.composite(ByteBufCodecs.VAR_LONG.apply(ByteBufCodecs.list()),RemoteBinding::terminals,ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),RemoteBinding::dims,RemoteBinding::new);

    public boolean contain(BlockPos pos, String dim) {
        for(int i=0; i < terminals.size(); i++) {
            if(terminals.get(i) == pos.asLong() && dims.get(i).equals(dim)) {
                return true;
            }
        }
        return false;
    }

    public RemoteBinding add(BlockPos pos, String dim) {
        List<Long> terminal=new ArrayList<>(terminals);
        List<String> dimension=new ArrayList<>(dims);
        terminal.add(pos.asLong());
        dimension.add(dim);
        return new RemoteBinding(terminal,dimension);
    }

    public RemoteBinding remove(int index) {
        List<Long> terminal=new ArrayList<>(terminals);
        List<String> dimension=new ArrayList<>(dims);
        terminal.remove(index);
        dimension.remove(index);
        return new RemoteBinding(terminal,dimension);
    }

    public int size() {
        return Math.min(terminals.size(),dims.size());
    }

}

