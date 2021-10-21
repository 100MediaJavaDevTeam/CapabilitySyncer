package me.sizableshrimp.capabilitysyncer.network;

import me.sizableshrimp.capabilitysyncer.core.ISyncableCapability;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SimpleCapabilityStatusPacket extends CapabilityStatusPacket {
    private static final Map<ResourceLocation, Function<Entity, ISyncableCapability>> capRetrievers = new HashMap<>();
    private final ResourceLocation capabilityId;

    public SimpleCapabilityStatusPacket(int entityId, ResourceLocation capabilityId, CompoundNBT tag) {
        super(entityId, tag);
        this.capabilityId = capabilityId;
    }

    public SimpleCapabilityStatusPacket(int entityId, ResourceLocation capabilityId, ISyncableCapability capability) {
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
        context.enqueueWork(() -> ClientPacketHandler.handleCapabilityStatus(this, capRetrievers.get(capabilityId)));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> void register(ResourceLocation capabilityId, Function<T, ISyncableCapability> capabilityRetriever, SimpleChannel channel, int id) {
        capRetrievers.put(capabilityId, (Function<Entity, ISyncableCapability>) capabilityRetriever);
        register(channel, id, SimpleCapabilityStatusPacket.class, buf -> read(buf, (entityId, tag) -> new SimpleCapabilityStatusPacket(entityId, buf.readResourceLocation(), tag)));
    }
}
