package dev._100media.capabilitysyncer.network;

import dev._100media.capabilitysyncer.core.ISyncableCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SimpleCapabilityStatusPacket extends CapabilityStatusPacket {
    private static final Map<ResourceLocation, Function<Entity, ISyncableCapability>> capRetrievers = new HashMap<>();
    private final ResourceLocation capabilityId;

    public SimpleCapabilityStatusPacket(int entityId, ResourceLocation capabilityId, CompoundTag tag) {
        super(entityId, tag);
        this.capabilityId = capabilityId;
    }

    public SimpleCapabilityStatusPacket(int entityId, ResourceLocation capabilityId, ISyncableCapability capability) {
        super(entityId, capability);
        this.capabilityId = capabilityId;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        buf.writeResourceLocation(capabilityId);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientPacketHandler.handleCapabilityStatus(this, capRetrievers.get(capabilityId)));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> void register(ResourceLocation capabilityId, Function<T, ISyncableCapability> capabilityRetriever, SimpleChannel channel, int id) {
        capRetrievers.put(capabilityId, (Function<Entity, ISyncableCapability>) capabilityRetriever);
        register(channel, id, SimpleCapabilityStatusPacket.class, buf -> read(buf, (entityId, tag) -> new SimpleCapabilityStatusPacket(entityId, buf.readResourceLocation(), tag)));
    }
}
