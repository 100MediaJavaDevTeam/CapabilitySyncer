package dev._100media.capabilitysyncer.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Function;

public interface IPacket {
    static <T extends IPacket> void register(SimpleChannel channel, int id, NetworkDirection direction, Class<T> packetClass, Function<PacketBuffer, T> readFunc) {
        register(channel.messageBuilder(packetClass, id, direction), readFunc);
    }

    static <T extends IPacket> void register(SimpleChannel.MessageBuilder<T> builder, Function<PacketBuffer, T> readFunc) {
        builder.encoder(IPacket::write)
                .decoder(readFunc)
                .consumer((p, sup) -> {
                    p.handle(sup.get());
                    return true;
                })
                .add();
    }

    void handle(NetworkEvent.Context context);

    void write(PacketBuffer packetBuf);
}
