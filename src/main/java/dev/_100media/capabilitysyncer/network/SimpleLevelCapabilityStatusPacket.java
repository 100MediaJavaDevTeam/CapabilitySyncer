package dev._100media.capabilitysyncer.network;

import dev._100media.capabilitysyncer.core.ISyncableCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SimpleLevelCapabilityStatusPacket extends CapabilityStatusPacket {
    private static final Map<ResourceLocation, Function<Level, ISyncableCapability>> capRetrievers = new HashMap<>();
    private final ResourceLocation capabilityId;

    public SimpleLevelCapabilityStatusPacket(int entityId, ResourceLocation capabilityId, CompoundTag tag) {
        super(entityId, tag);
        this.capabilityId = capabilityId;
    }

    public SimpleLevelCapabilityStatusPacket(int entityId, ResourceLocation capabilityId, ISyncableCapability capability) {
        super(entityId, capability);
        this.capabilityId = capabilityId;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Level> void register(ResourceLocation capabilityId, Function<T, ISyncableCapability> capabilityRetriever, SimpleChannel channel, int id) {
        capRetrievers.put(capabilityId, (Function<Level, ISyncableCapability>) capabilityRetriever);
        register(channel, id, SimpleLevelCapabilityStatusPacket.class, buf -> read(buf, (entityId, tag) -> new SimpleLevelCapabilityStatusPacket(entityId, buf.readResourceLocation(), tag)));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        buf.writeResourceLocation(capabilityId);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientPacketHandler.handleLevelCapabilityStatus(this, capRetrievers.get(capabilityId)));
    }
}
