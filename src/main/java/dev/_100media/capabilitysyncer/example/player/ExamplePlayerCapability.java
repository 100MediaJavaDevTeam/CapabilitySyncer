package dev._100media.capabilitysyncer.example.player;

import dev._100media.capabilitysyncer.core.PlayerCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.simple.SimpleChannel;

public class ExamplePlayerCapability extends PlayerCapability {
    private int exampleInt = 5;

    public ExamplePlayerCapability(Player player) {
        super(player);
    }

    public int getExampleInt() {
        return exampleInt;
    }

    public void setExampleInt(int exampleInt) {
        this.exampleInt = exampleInt;
    }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag nbt = new CompoundTag();

        nbt.putInt("ExampleInt", this.exampleInt);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        if (nbt.contains("ExampleInt", Tag.TAG_INT)) {
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
