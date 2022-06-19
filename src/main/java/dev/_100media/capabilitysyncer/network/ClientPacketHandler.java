package dev._100media.capabilitysyncer.network;

import dev._100media.capabilitysyncer.core.ISyncableCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.function.Function;

class ClientPacketHandler {
    static <T extends Entity> void handleEntityCapabilityStatus(EntityCapabilityStatusPacket packet, Function<T, ISyncableCapability> capabilityRetriever) {
        ISyncableCapability capability = capabilityRetriever.apply(getEntity(packet.getEntityId()));
        if (capability != null)
            capability.deserializeNBT(packet.getTag(), false);
    }
    static void handleLevelCapabilityStatus(LevelCapabilityStatusPacket packet, Function<World, ISyncableCapability> capabilityRetriever) {
        ISyncableCapability capability = capabilityRetriever.apply(Minecraft.getInstance().level);
        if (capability != null)
            capability.deserializeNBT(packet.getTag(), false);
    }
    private static <T extends Entity> T getEntity(int entityId) {
        ClientWorld level = Minecraft.getInstance().level;
        if (level == null)
            return null;
        Entity entity = level.getEntity(entityId);
        //noinspection unchecked
        return (T) entity;
    }
}
