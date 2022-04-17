package dev._100media.capabilitysyncer.core;

import net.minecraft.world.entity.player.Player;

public abstract class PlayerCapability extends LivingEntityCapability {
    protected final Player player;

    protected PlayerCapability(Player player) {
        super(player);
        this.player = player;
    }
}
