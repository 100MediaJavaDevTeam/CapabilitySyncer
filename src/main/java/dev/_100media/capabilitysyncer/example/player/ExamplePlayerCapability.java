package dev._100media.capabilitysyncer.example.player;

import dev._100media.capabilitysyncer.core.PlayerCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ExamplePlayerCapability extends PlayerCapability {
    private boolean cool = false;

    public ExamplePlayerCapability(PlayerEntity player) {
        super(player);
    }

    public boolean isCool() {
        return cool;
    }

    public void setCool(boolean cool) {
        this.cool = cool;
    }

    @Override
    public CompoundNBT serializeNBT(boolean savingToDisk) {
        CompoundNBT nbt = new CompoundNBT();

        nbt.putBoolean("Cool", this.cool);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt, boolean readingFromDisk) {
        this.cool = nbt.getBoolean("Cool");
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
