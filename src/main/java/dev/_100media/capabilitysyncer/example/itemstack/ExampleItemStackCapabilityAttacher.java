package dev._100media.capabilitysyncer.example.itemstack;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;

public class ExampleItemStackCapabilityAttacher extends CapabilityAttacher {
    private static final Class<ExampleItemStackCapability> CAPABILITY_CLASS = ExampleItemStackCapability.class;
    @CapabilityInject(ExampleItemStackCapability.class) // HAS to be public!
    public static final Capability<ExampleItemStackCapability> EXAMPLE_ITEM_STACK_CAPABILITY = null;
    public static final ResourceLocation EXAMPLE_PLAYER_CAPABILITY_RL = new ResourceLocation("example", "example_item_stack_capability");

    @Nullable
    public static ExampleItemStackCapability getExampleItemStackCapabilityUnwrap(ItemStack itemStack) {
        return getExampleItemStackCapability(itemStack).orElse(null);
    }

    public static LazyOptional<ExampleItemStackCapability> getExampleItemStackCapability(ItemStack itemStack) {
        return itemStack.getCapability(EXAMPLE_ITEM_STACK_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<ItemStack> event, ItemStack itemStack) {
        genericAttachCapability(event, new ExampleItemStackCapability(itemStack), EXAMPLE_ITEM_STACK_CAPABILITY, EXAMPLE_PLAYER_CAPABILITY_RL);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerItemStackAttacher(ExampleItemStackCapabilityAttacher::attach, ExampleItemStackCapabilityAttacher::getExampleItemStackCapability);
    }
}
