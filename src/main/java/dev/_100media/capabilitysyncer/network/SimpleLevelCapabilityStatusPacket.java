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

public class SimpleLevelCapabilityStatusPacket extends LevelCapabilityStatusPacket {
    private static final Map<ResourceLocation, Function<Level, ISyncableCapability>> capRetrievers = new HashMap<>();
    private final ResourceLocation capabilityId;

    public SimpleLevelCapabilityStatusPacket(ResourceLocation capabilityId, CompoundTag tag) {
        super(tag);
        this.capabilityId = capabilityId;
    }

    public SimpleLevelCapabilityStatusPacket(ResourceLocation capabilityId, ISyncableCapability capability) {
        super(capability);
        this.capabilityId = capabilityId;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Level> void register(ResourceLocation capabilityId, Function<T, ISyncableCapability> capabilityRetriever, SimpleChannel channel, int id) {
        capRetrievers.put(capabilityId, (Function<Level, ISyncableCapability>) capabilityRetriever);
        register(channel, id, SimpleLevelCapabilityStatusPacket.class, buf -> read(buf, tag -> new SimpleLevelCapabilityStatusPacket(buf.readResourceLocation(), tag)));
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
