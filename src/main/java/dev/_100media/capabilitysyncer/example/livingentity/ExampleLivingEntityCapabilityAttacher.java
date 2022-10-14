package dev._100media.capabilitysyncer.example.livingentity;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import dev._100media.capabilitysyncer.core.LivingEntityCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class ExampleLivingEntityCapabilityAttacher extends CapabilityAttacher {
    private static final Class<LivingEntityCapability> CAPABILITY_CLASS = LivingEntityCapability.class;
    public static final Capability<LivingEntityCapability> EXAMPLE_LIVING_ENTITY_CAPABILITY = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation EXAMPLE_LIVING_ENTITY_CAPABILITY_RL = new ResourceLocation("example", "example_living_entity_capability");

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static LivingEntityCapability getExampleLivingEntityCapabilityUnwrap(LivingEntity entity) {
        return getExampleLivingEntityCapability(entity).orElse(null);
    }

    public static LazyOptional<LivingEntityCapability> getExampleLivingEntityCapability(LivingEntity entity) {
        return entity.getCapability(EXAMPLE_LIVING_ENTITY_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<Entity> event, LivingEntity entity) {
        genericAttachCapability(event, new ExampleLivingEntityCapability(entity), EXAMPLE_LIVING_ENTITY_CAPABILITY, EXAMPLE_LIVING_ENTITY_CAPABILITY_RL);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerEntityAttacher(LivingEntity.class, ExampleLivingEntityCapabilityAttacher::attach, ExampleLivingEntityCapabilityAttacher::getExampleLivingEntityCapability, true);
    }
}
