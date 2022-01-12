package dev._100media.capabilitysyncer.example.itemstack;

import dev._100media.capabilitysyncer.core.ItemStackCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ExampleItemStackCapability extends ItemStackCapability {
    private boolean cool = false;

    public ExampleItemStackCapability(ItemStack itemStack) {
        super(itemStack);
    }

    public boolean isCool() {
        return cool;
    }

    public void setCool(boolean cool) {
        this.cool = cool;
    }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag nbt = new CompoundTag();

        nbt.putBoolean("Cool", this.cool);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        this.cool = nbt.getBoolean("Cool");
    }
}
