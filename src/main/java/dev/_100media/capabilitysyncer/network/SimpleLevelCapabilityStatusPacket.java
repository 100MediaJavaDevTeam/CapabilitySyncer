package dev._100media.capabilitysyncer.network;

import dev._100media.capabilitysyncer.core.ISyncableCapability;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SimpleLevelCapabilityStatusPacket extends LevelCapabilityStatusPacket {
    private static final Map<ResourceLocation, Function<World, ISyncableCapability>> capRetrievers = new HashMap<>();
    private final ResourceLocation capabilityId;

    public SimpleLevelCapabilityStatusPacket(ResourceLocation capabilityId, CompoundNBT tag) {
        super(tag);
        this.capabilityId = capabilityId;
    }

    public SimpleLevelCapabilityStatusPacket(ResourceLocation capabilityId, ISyncableCapability capability) {
        super(capability);
        this.capabilityId = capabilityId;
    }

    @SuppressWarnings("unchecked")
    public static <T extends World> void register(ResourceLocation capabilityId, Function<T, ISyncableCapability> capabilityRetriever, SimpleChannel channel, int id) {
        capRetrievers.put(capabilityId, (Function<World, ISyncableCapability>) capabilityRetriever);
        register(channel, id, SimpleLevelCapabilityStatusPacket.class, buf -> read(buf, tag -> new SimpleLevelCapabilityStatusPacket(buf.readResourceLocation(), tag)));
    }

    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);
        buf.writeResourceLocation(capabilityId);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientPacketHandler.handleLevelCapabilityStatus(this, capRetrievers.get(capabilityId)));
    }
}