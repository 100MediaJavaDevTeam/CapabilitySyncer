package dev._100media.capabilitysyncer.core;

import dev._100media.capabilitysyncer.network.LevelCapabilityStatusPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public abstract class LevelCapability implements ISyncableCapability {
    protected final World level;

    protected LevelCapability(World level) {
        this.level = level;
    }

    @Override
    public void updateTracking() {
        if (this.level.isClientSide)
            return;
        getNetworkChannel().send(PacketDistributor.DIMENSION.with(this.level::dimension), this.createUpdatePacket());
    }

    @Override
    public abstract LevelCapabilityStatusPacket createUpdatePacket();
}