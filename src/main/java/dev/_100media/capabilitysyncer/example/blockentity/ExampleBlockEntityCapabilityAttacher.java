package dev._100media.capabilitysyncer.example.blockentity;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class ExampleBlockEntityCapabilityAttacher extends CapabilityAttacher {
    private static final Class<ExampleBlockEntityCapability> CAPABILITY_CLASS = ExampleBlockEntityCapability.class;
    public static final Capability<ExampleBlockEntityCapability> EXAMPLE_BLOCK_ENTITY_CAPABILITY = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation EXAMPLE_BLOCK_ENTITY_CAPABILITY_RL = new ResourceLocation("example", "example_block_entity_capability");

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static ExampleBlockEntityCapability getExampleBlockEntityCapabilityUnwrap(BlockEntity blockEntity) {
        return getExampleBlockEntityCapability(blockEntity).orElse(null);
    }

    public static LazyOptional<ExampleBlockEntityCapability> getExampleBlockEntityCapability(BlockEntity blockEntity) {
        return blockEntity.getCapability(EXAMPLE_BLOCK_ENTITY_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<BlockEntity> event, BlockEntity blockEntity) {
        genericAttachCapability(event, new ExampleBlockEntityCapability(blockEntity), EXAMPLE_BLOCK_ENTITY_CAPABILITY, EXAMPLE_BLOCK_ENTITY_CAPABILITY_RL);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerBlockEntityAttacher(ExampleBlockEntityCapabilityAttacher::attach, ExampleBlockEntityCapabilityAttacher::getExampleBlockEntityCapability);
    }
}
