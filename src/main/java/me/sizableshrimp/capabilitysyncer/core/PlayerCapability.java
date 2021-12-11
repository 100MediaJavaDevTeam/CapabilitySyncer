package me.sizableshrimp.capabilitysyncer.core;

import net.minecraft.world.entity.player.Player;

public abstract class PlayerCapability extends EntityCapability {
    protected final Player player;

    protected PlayerCapability(Player player) {
        super(player);
        this.player = player;
    }
}
