package dev._100media.capabilitysyncer.network;

import dev._100media.capabilitysyncer.core.ISyncableCapability;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Function;

public abstract class LevelCapabilityStatusPacket implements IPacket {
    private final CompoundNBT tag;

    protected LevelCapabilityStatusPacket(CompoundNBT tag) {
        this.tag = tag;
    }

    protected LevelCapabilityStatusPacket(ISyncableCapability capability) {
        this(capability.serializeNBT(false));
    }

    protected static <T extends LevelCapabilityStatusPacket> void register(SimpleChannel channel, int id, Class<T> packetClass, Function<PacketBuffer, T> readFunc) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, packetClass, readFunc);
    }

    public void write(PacketBuffer buf) {
        buf.writeNbt(tag);
    }

    protected static <T extends LevelCapabilityStatusPacket> T read(PacketBuffer buf, Function<CompoundNBT, T> function) {
        return function.apply(buf.readNbt());
    }

    public CompoundNBT getTag() {
        return tag;
    }
}