package dev._100media.capabilitysyncer.example.livingentity;

import dev._100media.capabilitysyncer.core.LivingEntityCapability;
import dev._100media.capabilitysyncer.core.PlayerCapability;
import dev._100media.capabilitysyncer.example.player.ExamplePlayerCapabilityAttacher;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.simple.SimpleChannel;

public class ExampleLivingEntityCapability extends LivingEntityCapability {
    private int exampleInt = 5;

    public ExampleLivingEntityCapability(LivingEntity entity) {
        super(entity);
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
        return new SimpleEntityCapabilityStatusPacket(this.livingEntity.getId(), ExampleLivingEntityCapabilityAttacher.EXAMPLE_LIVING_ENTITY_CAPABILITY_RL, this);
    }

    @Override
    public SimpleChannel getNetworkChannel() {
        // Return the network channel here
        return null;
    }
}
