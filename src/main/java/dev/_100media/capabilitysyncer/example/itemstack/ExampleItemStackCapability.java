package dev._100media.capabilitysyncer.example.itemstack;

import dev._100media.capabilitysyncer.core.ItemStackCapability;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

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
}
