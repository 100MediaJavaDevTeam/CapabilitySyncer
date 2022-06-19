package dev._100media.capabilitysyncer.example.level;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;

public class ExampleLevelCapabilityAttacher extends CapabilityAttacher {
    private static final Class<ExampleLevelCapability> CAPABILITY_CLASS = ExampleLevelCapability.class;
    @CapabilityInject(ExampleLevelCapability.class)
    public static final Capability<ExampleLevelCapability> EXAMPLE_LEVEL_CAPABILITY = null;
    public static final ResourceLocation EXAMPLE_LEVEL_CAPABILITY_RL = new ResourceLocation("example", "example_level_capability");

    @Nullable
    public static ExampleLevelCapability getExampleLevelCapabilityUnwrap(World level) {
        return getExampleLevelCapability(level).orElse(null);
    }

    public static LazyOptional<ExampleLevelCapability> getExampleLevelCapability(World level) {
        return level.getCapability(EXAMPLE_LEVEL_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<World> event, World level) {
        genericAttachCapability(event, new ExampleLevelCapability(level), EXAMPLE_LEVEL_CAPABILITY, EXAMPLE_LEVEL_CAPABILITY_RL);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerLevelAttacher(ExampleLevelCapabilityAttacher::attach, ExampleLevelCapabilityAttacher::getExampleLevelCapability);
    }
}