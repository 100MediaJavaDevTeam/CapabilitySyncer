package dev._100media.capabilitysyncer.example.level;

import dev._100media.capabilitysyncer.core.LevelCapability;
import dev._100media.capabilitysyncer.network.LevelCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleLevelCapabilityStatusPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.simple.SimpleChannel;

public class ExampleLevelCapability extends LevelCapability {
    private int exampleInt = 5;

    public ExampleLevelCapability(Level level) {
        super(level);
    }

    public int getExampleInt() {
        return exampleInt;
    }

    public void setExampleInt(int exampleInt, boolean sync) {
        this.exampleInt = exampleInt;
        if (sync) {
            // Send an update packet to all clients in this dimension
            this.updateTracking();
        }
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
    public LevelCapabilityStatusPacket createUpdatePacket() {
        // Make sure to register this update packet to your network channel!
        return new SimpleLevelCapabilityStatusPacket(ExampleLevelCapabilityAttacher.EXAMPLE_LEVEL_CAPABILITY_RL, this);
    }

    @Override
    public SimpleChannel getNetworkChannel() {
        // Return the network channel here
        return null;
    }
}
