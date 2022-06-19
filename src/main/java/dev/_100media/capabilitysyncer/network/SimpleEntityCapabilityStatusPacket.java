package dev._100media.capabilitysyncer.network;

import dev._100media.capabilitysyncer.core.ISyncableCapability;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SimpleEntityCapabilityStatusPacket extends EntityCapabilityStatusPacket {
    private static final Map<ResourceLocation, Function<Entity, ISyncableCapability>> capRetrievers = new HashMap<>();
    private final ResourceLocation capabilityId;

    public SimpleEntityCapabilityStatusPacket(int entityId, ResourceLocation capabilityId, CompoundNBT tag) {
        super(entityId, tag);
        this.capabilityId = capabilityId;
    }

    public SimpleEntityCapabilityStatusPacket(int entityId, ResourceLocation capabilityId, ISyncableCapability capability) {
        super(entityId, capability);
        this.capabilityId = capabilityId;
    }

    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);
        buf.writeResourceLocation(capabilityId);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientPacketHandler.handleEntityCapabilityStatus(this, capRetrievers.get(capabilityId)));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> void register(ResourceLocation capabilityId, Function<T, ISyncableCapability> capabilityRetriever, SimpleChannel channel, int id) {
        capRetrievers.put(capabilityId, (Function<Entity, ISyncableCapability>) capabilityRetriever);
        register(channel, id, SimpleEntityCapabilityStatusPacket.class, buf -> read(buf, (entityId, tag) -> new SimpleEntityCapabilityStatusPacket(entityId, buf.readResourceLocation(), tag)));
    }
}
