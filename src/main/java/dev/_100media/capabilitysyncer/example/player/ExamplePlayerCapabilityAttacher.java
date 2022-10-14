package dev._100media.capabilitysyncer.example.player;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class ExamplePlayerCapabilityAttacher extends CapabilityAttacher {
    private static final Class<ExamplePlayerCapability> CAPABILITY_CLASS = ExamplePlayerCapability.class;
    public static final Capability<ExamplePlayerCapability> EXAMPLE_PLAYER_CAPABILITY = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation EXAMPLE_PLAYER_CAPABILITY_RL = new ResourceLocation("example", "example_player_capability");

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static ExamplePlayerCapability getExamplePlayerCapabilityUnwrap(Player player) {
        return getExamplePlayerCapability(player).orElse(null);
    }

    public static LazyOptional<ExamplePlayerCapability> getExamplePlayerCapability(Player player) {
        return player.getCapability(EXAMPLE_PLAYER_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<Entity> event, Player player) {
        genericAttachCapability(event, new ExamplePlayerCapability(player), EXAMPLE_PLAYER_CAPABILITY, EXAMPLE_PLAYER_CAPABILITY_RL);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerPlayerAttacher(ExamplePlayerCapabilityAttacher::attach, ExamplePlayerCapabilityAttacher::getExamplePlayerCapability, true);
    }
}
