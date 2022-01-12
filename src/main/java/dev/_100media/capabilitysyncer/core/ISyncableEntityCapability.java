package dev._100media.capabilitysyncer.core;

import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public interface ISyncableEntityCapability extends INBTSavable<CompoundNBT> {
    void updateTracking();

    EntityCapabilityStatusPacket createUpdatePacket();

    default void sendUpdatePacketToPlayer(ServerPlayerEntity serverPlayer) {
        getNetworkChannel().send(PacketDistributor.PLAYER.with(() -> serverPlayer), this.createUpdatePacket());
    }

    SimpleChannel getNetworkChannel();
}
