package dev._100media.capabilitysyncer.core;

import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public interface ISyncableEntityCapability extends INBTSavable<CompoundTag> {
    void updateTracking();

    EntityCapabilityStatusPacket createUpdatePacket();

    default void sendUpdatePacketToPlayer(ServerPlayer serverPlayer) {
        getNetworkChannel().send(PacketDistributor.PLAYER.with(() -> serverPlayer), this.createUpdatePacket());
    }

    SimpleChannel getNetworkChannel();
}