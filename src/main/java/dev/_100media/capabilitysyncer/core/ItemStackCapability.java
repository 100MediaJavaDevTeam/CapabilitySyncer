package dev._100media.capabilitysyncer.core;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public abstract class ItemStackCapability implements INBTSavable<CompoundNBT> {
    protected final ItemStack itemStack;

    protected ItemStackCapability(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}
