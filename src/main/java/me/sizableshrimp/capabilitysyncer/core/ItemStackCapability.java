package me.sizableshrimp.capabilitysyncer.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public abstract class ItemStackCapability implements INBTSavable<CompoundTag>{

    protected final ItemStack itemStack;

    public ItemStackCapability(ItemStack itemStack){
        this.itemStack = itemStack;
    }
}
