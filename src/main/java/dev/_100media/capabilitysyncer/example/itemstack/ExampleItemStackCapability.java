package dev._100media.capabilitysyncer.example.itemstack;

import dev._100media.capabilitysyncer.core.ItemStackCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class ExampleItemStackCapability extends ItemStackCapability {
    private int exampleInt = 5;

    public ExampleItemStackCapability(ItemStack itemStack) {
        super(itemStack);
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
}
