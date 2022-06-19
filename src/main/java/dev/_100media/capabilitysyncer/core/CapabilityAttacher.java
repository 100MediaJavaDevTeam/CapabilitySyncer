package dev._100media.capabilitysyncer.core;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class CapabilityAttacher {
    @SuppressWarnings("rawtypes")
    private static final Capability.IStorage EMPTY_STORAGE = new Capability.IStorage() {
        @Nullable
        @Override
        public INBT writeNBT(Capability capability, Object instance, Direction side) {return new CompoundNBT();}

        @Override
        public void readNBT(Capability capability, Object instance, Direction side, INBT nbt) {}
    };

    static {
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, CapabilityAttacher::onAttachEntityCapability);
        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, CapabilityAttacher::onAttachItemStackCapability);
        MinecraftForge.EVENT_BUS.addGenericListener(World.class, CapabilityAttacher::onAttachLevelCapability);
        MinecraftForge.EVENT_BUS.addListener(CapabilityAttacher::onEntityJoinWorld);
        MinecraftForge.EVENT_BUS.addListener(CapabilityAttacher::onPlayerStartTracking);
        MinecraftForge.EVENT_BUS.addListener(CapabilityAttacher::onPlayerClone);
    }

    @SuppressWarnings("unchecked")
    protected static <T> void registerCapability(Class<T> capClass) {
        CapabilityManager.INSTANCE.register(capClass, (Capability.IStorage<T>) EMPTY_STORAGE, () -> null);
    }

    private static final List<BiConsumer<AttachCapabilitiesEvent<Entity>, Entity>> entityCapAttachers = new ArrayList<>();
    private static final List<Function<Entity, LazyOptional<? extends ISyncableCapability>>> entityCapRetrievers = new ArrayList<>();

    private static final List<BiConsumer<AttachCapabilitiesEvent<ItemStack>, ItemStack>> itemStackCapAttachers = new ArrayList<>();
    private static final List<Function<ItemStack, LazyOptional<? extends ItemStackCapability>>> itemStackCapRetrievers = new ArrayList<>();

    private static final List<BiConsumer<AttachCapabilitiesEvent<World>, World>> levelCapAttachers = new ArrayList<>();
    private static final List<Function<World, LazyOptional<? extends ISyncableCapability>>> levelCapRetrievers = new ArrayList<>();
    private static final List<BiConsumer<PlayerEntity, PlayerEntity>> playerCapCloners = new ArrayList<>();

    protected static <C extends ISyncableCapability> void registerPlayerAttacher(BiConsumer<AttachCapabilitiesEvent<Entity>, PlayerEntity> attacher,
                                                                                 Function<PlayerEntity, LazyOptional<C>> capRetriever, boolean copyOnDeath) {
        registerEntityAttacher(PlayerEntity.class, attacher, capRetriever);
        if (copyOnDeath) {
            playerCapCloners.add((oldPlayer, newPlayer) -> capRetriever.apply(oldPlayer).ifPresent(oldCap -> capRetriever.apply(newPlayer)
                    .ifPresent(newCap -> newCap.deserializeNBT(oldCap.serializeNBT(false), false))));
        }
    }

    @SuppressWarnings("unchecked")
    protected static <E extends Entity, C extends ISyncableCapability> void registerEntityAttacher(Class<E> entityClass, BiConsumer<AttachCapabilitiesEvent<Entity>, E> attacher,
                                                                                                   Function<E, LazyOptional<C>> capRetriever) {
        entityCapAttachers.add((event, entity) -> {
            if (entityClass.isInstance(entity))
                attacher.accept(event, (E) entity);
        });
        entityCapRetrievers.add(entity -> entityClass.isInstance(entity) ? capRetriever.apply((E) entity) : LazyOptional.empty());
    }

    protected static <C extends ItemStackCapability> void registerItemStackAttacher(BiConsumer<AttachCapabilitiesEvent<ItemStack>, ItemStack> attacher,
            Function<ItemStack, LazyOptional<C>> capRetriever) {
        itemStackCapAttachers.add(attacher);
        itemStackCapRetrievers.add(capRetriever::apply);
    }
    protected static <C extends ISyncableCapability> void registerLevelAttacher(BiConsumer<AttachCapabilitiesEvent<World>, World> attacher,
                                                                                Function<World, LazyOptional<C>> capRetriever) {
        registerLevelAttacher(World.class, attacher, capRetriever);
    }
    @SuppressWarnings("unchecked")
    protected static <E extends World, C extends ISyncableCapability> void registerLevelAttacher(Class<E> levelClass, BiConsumer<AttachCapabilitiesEvent<World>, E> attacher,
                                                                                                 Function<E, LazyOptional<C>> capRetriever) {
        levelCapAttachers.add((event, level) -> {
            if (levelClass.isInstance(level))
                attacher.accept(event, (E) level);
        });
        levelCapRetrievers.add(level -> levelClass.isInstance(level) ? capRetriever.apply((E) level) : LazyOptional.empty());
    }
    protected static <I extends INBTSerializable<T>, T extends INBT> void genericAttachCapability(AttachCapabilitiesEvent<?> event, I impl, Capability<I> capability, ResourceLocation location) {
        genericAttachCapability(event, impl, capability, location, true);
    }

    protected static <I extends INBTSerializable<T>, T extends INBT> void genericAttachCapability(AttachCapabilitiesEvent<?> event, I impl, Capability<I> capability, ResourceLocation location,
            boolean save) {
        LazyOptional<I> storage = LazyOptional.of(() -> impl);
        ICapabilityProvider provider = getProvider(impl, storage, capability, save);
        event.addCapability(location, provider);
        event.addListener(storage::invalidate);
    }

    protected static <I extends INBTSerializable<T>, T extends INBT> ICapabilityProvider getProvider(I impl, LazyOptional<I> storage, Capability<I> capability, boolean save) {
        if (capability == null)
            throw new NullPointerException();
        return save ? new ICapabilitySerializable<T>() {
            @Nonnull
            @Override
            public <C> LazyOptional<C> getCapability(@Nonnull Capability<C> cap, @Nullable Direction side) {
                return cap == capability ? storage.cast() : LazyOptional.empty();
            }

            @SuppressWarnings("unchecked")
            @Override
            public T serializeNBT() {
                return impl instanceof INBTSavable ? (T) ((INBTSavable<?>) impl).serializeNBT(true) : impl.serializeNBT();
            }

            @SuppressWarnings("unchecked")
            @Override
            public void deserializeNBT(T nbt) {
                if (impl instanceof INBTSavable) {
                    ((INBTSavable<T>) impl).deserializeNBT(nbt, true);
                } else {
                    impl.deserializeNBT(nbt);
                }
            }
        } : new ICapabilityProvider() {
            @Nonnull
            @Override
            public <C> LazyOptional<C> getCapability(@Nonnull Capability<C> cap, @Nullable Direction side) {
                return cap == capability ? storage.cast() : LazyOptional.empty();
            }
        };
    }

    private static void onAttachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
        // Attaches the entity capabilities
        entityCapAttachers.forEach(attacher -> attacher.accept(event, event.getObject()));
    }

    private static void onAttachItemStackCapability(AttachCapabilitiesEvent<ItemStack> event) {
        // Attaches the item stack capabilities
        itemStackCapAttachers.forEach(attacher -> attacher.accept(event, event.getObject()));
    }
    private static void onAttachLevelCapability(AttachCapabilitiesEvent<World> event) {
        // Attaches the level capabilities
        levelCapAttachers.forEach(attacher -> attacher.accept(event, event.getObject()));
    }
    private static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            // Syncs a player's capabilities to themselves on world join (either joining server or switching worlds)
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getEntity();
            entityCapRetrievers.forEach(capRetriever -> capRetriever.apply(serverPlayer).ifPresent(cap -> cap.sendUpdatePacketToPlayer(serverPlayer)));

            // Sync level capabilities to a player when they join that level
            levelCapRetrievers.forEach(capRetriever -> capRetriever.apply(serverPlayer.level).ifPresent(cap -> cap.sendUpdatePacketToPlayer(serverPlayer)));
        }
    }

    private static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        // Syncs an entity's capabilities to a player when they start tracking it
        ServerPlayerEntity currentPlayer = (ServerPlayerEntity) event.getPlayer();
        entityCapRetrievers.forEach(capRetriever -> capRetriever.apply(event.getTarget()).ifPresent(cap -> cap.sendUpdatePacketToPlayer(currentPlayer)));
    }

    private static void onPlayerClone(PlayerEvent.Clone event) {
        PlayerEntity oldPlayer = event.getOriginal();
        PlayerEntity newPlayer = event.getPlayer();

        // So we can copy capabilities
        oldPlayer.revive();

        playerCapCloners.forEach(capCloner -> capCloner.accept(oldPlayer, newPlayer));
    }
}
