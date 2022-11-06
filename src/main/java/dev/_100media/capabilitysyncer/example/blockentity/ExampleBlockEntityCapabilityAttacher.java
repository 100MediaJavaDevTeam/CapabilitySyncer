package dev._100media.capabilitysyncer.example.blockentity;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class ExampleBlockEntityCapabilityAttacher extends CapabilityAttacher {
    private static final Class<ExampleBlockEntityCapability> CAPABILITY_CLASS = ExampleBlockEntityCapability.class;
    public static final Capability<ExampleBlockEntityCapability> EXAMPLE_ITEM_STACK_CAPABILITY = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation EXAMPLE_ITEM_STACK_CAPABILITY_RL = new ResourceLocation("example", "example_item_stack_capability");

    @Nullable
    public static ExampleBlockEntityCapability getExampleBlockEntityCapabilityUnwrap(BlockEntity itemStack) {
        return getExampleBlockEntityCapability(itemStack).orElse(null);
    }

    public static LazyOptional<ExampleBlockEntityCapability> getExampleBlockEntityCapability(BlockEntity itemStack) {
        return itemStack.getCapability(EXAMPLE_ITEM_STACK_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<BlockEntity> event, BlockEntity itemStack) {
        genericAttachCapability(event, new ExampleBlockEntityCapability(itemStack), EXAMPLE_ITEM_STACK_CAPABILITY, EXAMPLE_ITEM_STACK_CAPABILITY_RL);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerBlockEntityAttacher(ExampleBlockEntityCapabilityAttacher::attach, ExampleBlockEntityCapabilityAttacher::getExampleBlockEntityCapability);
    }
}
