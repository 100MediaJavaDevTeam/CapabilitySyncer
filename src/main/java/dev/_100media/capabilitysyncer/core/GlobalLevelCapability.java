package dev._100media.capabilitysyncer.core;

import dev._100media.capabilitysyncer.network.LevelCapabilityStatusPacket;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

/**
 * A global level capability is attached to the overworld level on the server and the client level on the client.
 * It stores data that should be synced regardless of a player's dimension.
 */
public abstract class GlobalLevelCapability extends LevelCapability {
    protected GlobalLevelCapability(Level level) {
        super(level);
    }

    @Override
    public void updateTracking() {
        if (this.level.isClientSide)
            return;

        getNetworkChannel().send(PacketDistributor.ALL.noArg(), this.createUpdatePacket());
    }

    @Override
    public abstract LevelCapabilityStatusPacket createUpdatePacket();
}
