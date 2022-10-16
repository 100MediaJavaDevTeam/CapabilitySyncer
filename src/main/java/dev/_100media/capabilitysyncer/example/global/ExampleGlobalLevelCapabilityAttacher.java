package dev._100media.capabilitysyncer.example.global;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class ExampleGlobalLevelCapabilityAttacher extends CapabilityAttacher {
    private static final Class<ExampleGlobalLevelCapability> CAPABILITY_CLASS = ExampleGlobalLevelCapability.class;
    public static final Capability<ExampleGlobalLevelCapability> EXAMPLE_GLOBAL_LEVEL_CAPABILITY = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation EXAMPLE_GLOBAL_LEVEL_CAPABILITY_RL = new ResourceLocation("example", "example_global_level_capability");

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static ExampleGlobalLevelCapability getExampleGlobalLevelCapabilityUnwrap(Level level) {
        return getExampleGlobalLevelCapability(level).orElse(null);
    }

    public static LazyOptional<ExampleGlobalLevelCapability> getExampleGlobalLevelCapability(Level level) {
        return getGlobalLevelCapability(level, EXAMPLE_GLOBAL_LEVEL_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<Level> event, Level level) {
        genericAttachCapability(event, new ExampleGlobalLevelCapability(level), EXAMPLE_GLOBAL_LEVEL_CAPABILITY, EXAMPLE_GLOBAL_LEVEL_CAPABILITY_RL);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerGlobalLevelAttacher(ExampleGlobalLevelCapabilityAttacher::attach, ExampleGlobalLevelCapabilityAttacher::getExampleGlobalLevelCapability);
    }
}
