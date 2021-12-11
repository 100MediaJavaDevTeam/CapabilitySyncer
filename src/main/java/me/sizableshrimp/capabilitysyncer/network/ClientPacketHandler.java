package me.sizableshrimp.capabilitysyncer.network;

import me.sizableshrimp.capabilitysyncer.core.ISyncableCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

import java.util.function.Function;

class ClientPacketHandler {
    static <T extends Entity> void handleCapabilityStatus(CapabilityStatusPacket packet, Function<T, ISyncableCapability> capabilityRetriever) {
        ISyncableCapability capability = capabilityRetriever.apply(getEntity(packet.getEntityId()));
        if (capability != null)
            capability.deserializeNBT(packet.getTag(), false);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> T getEntity(int entityId) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return null;
        Entity entity = level.getEntity(entityId);
        return (T) entity;
    }
}
