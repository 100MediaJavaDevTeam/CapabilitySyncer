package dev._100media.capabilitysyncer.core;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.network.PacketDistributor;

public abstract class EntityCapability implements ISyncableCapability {
    protected final Entity entity;

    protected EntityCapability(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void updateTracking() {
        if (this.entity.level.isClientSide)
            return;
        getNetworkChannel().send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.entity), this.createUpdatePacket());
    }
}
