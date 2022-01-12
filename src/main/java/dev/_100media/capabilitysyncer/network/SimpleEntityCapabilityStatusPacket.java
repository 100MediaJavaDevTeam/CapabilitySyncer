package dev._100media.capabilitysyncer.network;

import dev._100media.capabilitysyncer.core.ISyncableEntityCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SimpleEntityCapabilityStatusPacket extends EntityCapabilityStatusPacket {
    private static final Map<ResourceLocation, Function<Entity, ISyncableEntityCapability>> capRetrievers = new HashMap<>();
    private final ResourceLocation capabilityId;

    public SimpleEntityCapabilityStatusPacket(int entityId, ResourceLocation capabilityId, CompoundTag tag) {
        super(entityId, tag);
        this.capabilityId = capabilityId;
    }

    public SimpleEntityCapabilityStatusPacket(int entityId, ResourceLocation capabilityId, ISyncableEntityCapability capability) {
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
    public static <T extends Entity> void register(ResourceLocation capabilityId, Function<T, ISyncableEntityCapability> capabilityRetriever, SimpleChannel channel, int id) {
        capRetrievers.put(capabilityId, (Function<Entity, ISyncableEntityCapability>) capabilityRetriever);
        register(channel, id, SimpleEntityCapabilityStatusPacket.class, buf -> read(buf, (entityId, tag) -> new SimpleEntityCapabilityStatusPacket(entityId, buf.readResourceLocation(), tag)));
    }
}
