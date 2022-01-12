package dev._100media.capabilitysyncer.network;

import dev._100media.capabilitysyncer.core.ISyncableEntityCapability;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class EntityCapabilityStatusPacket implements IPacket {
    private final int entityId;
    private final CompoundNBT tag;

    protected EntityCapabilityStatusPacket(int entityId, CompoundNBT tag) {
        this.entityId = entityId;
        this.tag = tag;
    }

    protected EntityCapabilityStatusPacket(int entityId, ISyncableEntityCapability capability) {
        this(entityId, capability.serializeNBT(false));
    }

    protected static <T extends EntityCapabilityStatusPacket> void register(SimpleChannel channel, int id, Class<T> packetClass, Function<PacketBuffer, T> readFunc) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, packetClass, readFunc);
    }

    public void write(PacketBuffer buf) {
        buf.writeInt(entityId);
        buf.writeNbt(tag);
    }

    protected static <T extends EntityCapabilityStatusPacket> T read(PacketBuffer buf, BiFunction<Integer, CompoundNBT, T> function) {
        return function.apply(buf.readInt(), buf.readNbt());
    }

    public int getEntityId() {
        return entityId;
    }

    public CompoundNBT getTag() {
        return tag;
    }
}
