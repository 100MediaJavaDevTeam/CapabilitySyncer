package dev._100media.capabilitysyncer.core;

import net.minecraft.world.entity.LivingEntity;

public abstract class LivingEntityCapability extends EntityCapability {
    protected final LivingEntity livingEntity;

    protected LivingEntityCapability(LivingEntity entity) {
        super(entity);
        this.livingEntity = entity;
    }
}
