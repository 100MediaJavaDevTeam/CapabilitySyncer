package dev._100media.capabilitysyncer.core;

import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

public abstract class LevelCapability implements ISyncableCapability {
    protected final Level level;

    protected LevelCapability(Level level) {
        this.level = level;
    }

    @Override
    public void updateTracking() {
        if (this.level.isClientSide)
            return;
        level.players().forEach(player -> getNetworkChannel().send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), this.createUpdatePacket(player.getId())));
    }
}
