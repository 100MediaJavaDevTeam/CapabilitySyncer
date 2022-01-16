package dev._100media.capabilitysyncer.example.player;

import dev._100media.capabilitysyncer.core.PlayerCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ExamplePlayerCapability extends PlayerCapability {
    private int exampleInt = 5;

    public ExamplePlayerCapability(PlayerEntity player) {
        super(player);
    }

    public int getExampleInt() {
        return exampleInt;
    }

    public void setExampleInt(int exampleInt, boolean sync) {
        this.exampleInt = exampleInt;
        if (sync) {
            // Send an update packet to all tracking clients
            this.updateTracking();
        }
    }

    @Override
    public CompoundNBT serializeNBT(boolean savingToDisk) {
        CompoundNBT nbt = new CompoundNBT();

        nbt.putInt("ExampleInt", this.exampleInt);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt, boolean readingFromDisk) {
        if (nbt.contains("ExampleInt", Constants.NBT.TAG_INT)) {
            this.exampleInt = nbt.getInt("ExampleInt");
        } else {
            this.exampleInt = 5;
        }
    }

    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        // Make sure to register this update packet to your network channel!
        return new SimpleEntityCapabilityStatusPacket(player.getId(), ExamplePlayerCapabilityAttacher.EXAMPLE_PLAYER_CAPABILITY_RL, this);
    }

    @Override
    public SimpleChannel getNetworkChannel() {
        // Return the network channel here
        return null;
    }
}
