package dev._100media.capabilitysyncer.example.player;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;

public class ExamplePlayerCapabilityAttacher extends CapabilityAttacher {
    private static final Class<ExamplePlayerCapability> CAPABILITY_CLASS = ExamplePlayerCapability.class;
    @CapabilityInject(ExamplePlayerCapability.class) // HAS to be public!
    public static final Capability<ExamplePlayerCapability> EXAMPLE_PLAYER_CAPABILITY = null;
    public static final ResourceLocation EXAMPLE_PLAYER_CAPABILITY_RL = new ResourceLocation("example", "example_player_capability");

    @Nullable
    public static ExamplePlayerCapability getExamplePlayerCapabilityUnwrap(PlayerEntity player) {
        return getExamplePlayerCapability(player).orElse(null);
    }

    public static LazyOptional<ExamplePlayerCapability> getExamplePlayerCapability(PlayerEntity player) {
        return player.getCapability(EXAMPLE_PLAYER_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<Entity> event, PlayerEntity player) {
        genericAttachCapability(event, new ExamplePlayerCapability(player), EXAMPLE_PLAYER_CAPABILITY, EXAMPLE_PLAYER_CAPABILITY_RL);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerPlayerAttacher(ExamplePlayerCapabilityAttacher::attach, ExamplePlayerCapabilityAttacher::getExamplePlayerCapability, true);
    }
}
