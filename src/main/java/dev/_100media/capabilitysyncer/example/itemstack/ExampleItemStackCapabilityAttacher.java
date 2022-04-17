package dev._100media.capabilitysyncer.example.itemstack;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class ExampleItemStackCapabilityAttacher extends CapabilityAttacher {
    private static final Class<ExampleItemStackCapability> CAPABILITY_CLASS = ExampleItemStackCapability.class;
    public static final Capability<ExampleItemStackCapability> EXAMPLE_ITEM_STACK_CAPABILITY = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation EXAMPLE_ITEM_STACK_CAPABILITY_RL = new ResourceLocation("example", "example_item_stack_capability");

    @Nullable
    public static ExampleItemStackCapability getExampleItemStackCapabilityUnwrap(ItemStack itemStack) {
        return getExampleItemStackCapability(itemStack).orElse(null);
    }

    public static LazyOptional<ExampleItemStackCapability> getExampleItemStackCapability(ItemStack itemStack) {
        return itemStack.getCapability(EXAMPLE_ITEM_STACK_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<ItemStack> event, ItemStack itemStack) {
        genericAttachCapability(event, new ExampleItemStackCapability(itemStack), EXAMPLE_ITEM_STACK_CAPABILITY, EXAMPLE_ITEM_STACK_CAPABILITY_RL);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerItemStackAttacher(ExampleItemStackCapabilityAttacher::attach, ExampleItemStackCapabilityAttacher::getExampleItemStackCapability);
    }
}
