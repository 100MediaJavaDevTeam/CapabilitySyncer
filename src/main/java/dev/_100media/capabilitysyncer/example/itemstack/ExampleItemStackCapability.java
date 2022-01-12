package dev._100media.capabilitysyncer.example.itemstack;

import dev._100media.capabilitysyncer.core.ItemStackCapability;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

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
    public CompoundNBT serializeNBT(boolean savingToDisk) {
        CompoundNBT nbt = new CompoundNBT();

        nbt.putBoolean("Cool", this.cool);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt, boolean readingFromDisk) {
        this.cool = nbt.getBoolean("Cool");
    }
}
