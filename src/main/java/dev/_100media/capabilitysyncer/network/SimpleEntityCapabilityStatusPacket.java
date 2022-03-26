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

public class SimpleEntityCapabilityStatusPacket extends CapabilityStatusPacket {
    private static final Map<ResourceLocation, Function<Entity, ISyncableCapability>> capRetrievers = new HashMap<>();
    private final ResourceLocation capabilityId;

    public SimpleEntityCapabilityStatusPacket(int entityId, ResourceLocation capabilityId, CompoundTag tag) {
        super(entityId, tag);
        this.capabilityId = capabilityId;
    }

    public SimpleEntityCapabilityStatusPacket(int entityId, ResourceLocation capabilityId, ISyncableCapability capability) {
        super(entityId, capability);
        this.capabilityId = capabilityId;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        buf.writeResourceLocation(capabilityId);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> void register(ResourceLocation capabilityId, Function<T, ISyncableCapability> capabilityRetriever, SimpleChannel channel, int id) {
        capRetrievers.put(capabilityId, (Function<Entity, ISyncableCapability>) capabilityRetriever);
        register(channel, id, SimpleEntityCapabilityStatusPacket.class, buf -> read(buf, (entityId, tag) -> new SimpleEntityCapabilityStatusPacket(entityId, buf.readResourceLocation(), tag)));
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientPacketHandler.handleEntityCapabilityStatus(this, capRetrievers.get(capabilityId)));
    }
}
