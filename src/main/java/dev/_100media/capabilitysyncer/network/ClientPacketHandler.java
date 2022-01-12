package dev._100media.capabilitysyncer.network;

import dev._100media.capabilitysyncer.core.ISyncableEntityCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

import java.util.function.Function;

class ClientPacketHandler {
    static <T extends Entity> void handleEntityCapabilityStatus(EntityCapabilityStatusPacket packet, Function<T, ISyncableEntityCapability> capabilityRetriever) {
        ISyncableEntityCapability capability = capabilityRetriever.apply(getEntity(packet.getEntityId()));
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
