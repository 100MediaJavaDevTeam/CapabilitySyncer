package dev._100media.capabilitysyncer.network;

import dev._100media.capabilitysyncer.core.ISyncableEntityCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class EntityCapabilityStatusPacket implements IPacket {
    private final int entityId;
    private final CompoundTag tag;

    protected EntityCapabilityStatusPacket(int entityId, CompoundTag tag) {
        this.entityId = entityId;
        this.tag = tag;
    }

    protected EntityCapabilityStatusPacket(int entityId, ISyncableEntityCapability capability) {
        this(entityId, capability.serializeNBT(false));
    }

    protected static <T extends EntityCapabilityStatusPacket> void register(SimpleChannel channel, int id, Class<T> packetClass, Function<FriendlyByteBuf, T> readFunc) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, packetClass, readFunc);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeNbt(tag);
    }

    protected static <T extends EntityCapabilityStatusPacket> T read(FriendlyByteBuf buf, BiFunction<Integer, CompoundTag, T> function) {
        return function.apply(buf.readInt(), buf.readNbt());
    }

    public int getEntityId() {
        return entityId;
    }

    public CompoundTag getTag() {
        return tag;
    }
}
