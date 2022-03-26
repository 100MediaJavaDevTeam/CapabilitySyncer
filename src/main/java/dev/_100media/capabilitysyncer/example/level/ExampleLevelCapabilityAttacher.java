package dev._100media.capabilitysyncer.example.level;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class ExampleLevelCapabilityAttacher extends CapabilityAttacher {
    private static final Class<ExampleLevelCapability> CAPABILITY_CLASS = ExampleLevelCapability.class;
    public static final Capability<ExampleLevelCapability> EXAMPLE_LEVEL_CAPABILITY = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation EXAMPLE_LEVEL_CAPABILITY_RL = new ResourceLocation("example", "example_level_capability");

    @Nullable
    public static ExampleLevelCapability getExampleLevelCapabilityUnwrap(Level level) {
        return getExampleLevelCapability(level).orElse(null);
    }

    public static LazyOptional<ExampleLevelCapability> getExampleLevelCapability(Level level) {
        return level.getCapability(EXAMPLE_LEVEL_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<Level> event, Level level) {
        genericAttachCapability(event, new ExampleLevelCapability(level), EXAMPLE_LEVEL_CAPABILITY, EXAMPLE_LEVEL_CAPABILITY_RL);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerLevelAttacher(ExampleLevelCapabilityAttacher::attach, ExampleLevelCapabilityAttacher::getExampleLevelCapability);
    }
}
