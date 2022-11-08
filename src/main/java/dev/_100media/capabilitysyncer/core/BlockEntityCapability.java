package dev._100media.capabilitysyncer.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BlockEntityCapability implements INBTSavable<CompoundTag> {
    protected final BlockEntity blockEntity;

    protected BlockEntityCapability(BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }
}
