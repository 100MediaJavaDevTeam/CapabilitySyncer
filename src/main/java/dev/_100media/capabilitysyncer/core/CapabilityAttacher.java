package dev._100media.capabilitysyncer.core;

import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class CapabilityAttacher {
    static {
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, CapabilityAttacher::onAttachEntityCapability);
        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, CapabilityAttacher::onAttachItemStackCapability);
        MinecraftForge.EVENT_BUS.addGenericListener(Level.class, CapabilityAttacher::onAttachLevelCapability);
        MinecraftForge.EVENT_BUS.addListener(CapabilityAttacher::onEntityJoinWorld);
        MinecraftForge.EVENT_BUS.addListener(CapabilityAttacher::onPlayerStartTracking);
        MinecraftForge.EVENT_BUS.addListener(CapabilityAttacher::onPlayerClone);

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(CapabilityAttacher::onRegisterCapabilities);
    }

    private static final List<Class<?>> capClasses = new ArrayList<>();

    @NotNull
    protected static <T> Capability<T> getCapability(CapabilityToken<T> type) {
        return CapabilityManager.get(type);
    }

    protected static <T> void registerCapability(Class<T> capClass) {
        capClasses.add(capClass);
    }

    private static final List<BiConsumer<AttachCapabilitiesEvent<Entity>, Entity>> entityCapAttachers = new ArrayList<>();
    private static final List<Function<Entity, LazyOptional<? extends ISyncableCapability>>> entityCapRetrievers = new ArrayList<>();

    private static final List<BiConsumer<AttachCapabilitiesEvent<Level>, Level>> levelCapAttachers = new ArrayList<>();
    private static final List<Function<Level, LazyOptional<? extends ISyncableCapability>>> levelCapRetrievers = new ArrayList<>();

    private static final List<BiConsumer<AttachCapabilitiesEvent<ItemStack>, ItemStack>> itemStackCapAttachers = new ArrayList<>();
    private static final List<Function<ItemStack, LazyOptional<? extends ItemStackCapability>>> itemStackCapRetrievers = new ArrayList<>();

    private static final List<BiConsumer<Player, Player>> playerCapCloners = new ArrayList<>();

    protected static <C extends ISyncableCapability> void registerPlayerAttacher(BiConsumer<AttachCapabilitiesEvent<Entity>, Player> attacher,
            Function<Player, LazyOptional<C>> capRetriever, boolean copyOnDeath) {
        registerEntityAttacher(Player.class, attacher, capRetriever);
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

    protected static <C extends ISyncableCapability> void registerLevelAttacher(BiConsumer<AttachCapabilitiesEvent<Level>, Level> attacher,
            Function<Level, LazyOptional<C>> capRetriever) {
        registerLevelAttacher(Level.class, attacher, capRetriever);
    }

    @SuppressWarnings("unchecked")
    protected static <E extends Level, C extends ISyncableCapability> void registerLevelAttacher(Class<E> levelClass, BiConsumer<AttachCapabilitiesEvent<Level>, E> attacher,
            Function<E, LazyOptional<C>> capRetriever) {
        levelCapAttachers.add((event, level) -> {
            if (levelClass.isInstance(level))
                attacher.accept(event, (E) level);
        });
        levelCapRetrievers.add(level -> levelClass.isInstance(level) ? capRetriever.apply((E) level) : LazyOptional.empty());
    }

    protected static <C extends ItemStackCapability> void registerItemStackAttacher(BiConsumer<AttachCapabilitiesEvent<ItemStack>, ItemStack> attacher,
            Function<ItemStack, LazyOptional<C>> capRetriever) {
        itemStackCapAttachers.add(attacher);
        itemStackCapRetrievers.add(capRetriever::apply);
    }

    protected static <I extends INBTSerializable<T>, T extends Tag> void genericAttachCapability(AttachCapabilitiesEvent<?> event, I impl, Capability<I> capability, ResourceLocation location) {
        genericAttachCapability(event, impl, capability, location, true);
    }

    protected static <I extends INBTSerializable<T>, T extends Tag> void genericAttachCapability(AttachCapabilitiesEvent<?> event, I impl, Capability<I> capability, ResourceLocation location,
            boolean save) {
        LazyOptional<I> storage = LazyOptional.of(() -> impl);
        ICapabilityProvider provider = getProvider(impl, storage, capability, save);
        event.addCapability(location, provider);
        // Forge 1.17/1.18 introduced a bug where invalidating the storage causes the capability data to be inaccessible from clone even with revive.
        // So, don't invalidate the storage.
        // event.addListener(storage::invalidate);
    }

    protected static <I extends INBTSerializable<T>, T extends Tag> ICapabilityProvider getProvider(I impl, LazyOptional<I> storage, Capability<I> capability, boolean save) {
        if (capability == null)
            throw new NullPointerException();
        return save ? new ICapabilitySerializable<T>() {
            @NotNull
            @Override
            public <C> LazyOptional<C> getCapability(@NotNull Capability<C> cap, @Nullable Direction side) {
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
            @NotNull
            @Override
            public <C> LazyOptional<C> getCapability(@NotNull Capability<C> cap, @Nullable Direction side) {
                return cap == capability ? storage.cast() : LazyOptional.empty();
            }
        };
    }

    private static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        // Registers the capability classes
        capClasses.forEach(event::register);
    }

    private static void onAttachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
        // Attaches the entity capabilities
        entityCapAttachers.forEach(attacher -> attacher.accept(event, event.getObject()));
    }

    private static void onAttachItemStackCapability(AttachCapabilitiesEvent<ItemStack> event) {
        // Attaches the item stack capabilities
        itemStackCapAttachers.forEach(attacher -> attacher.accept(event, event.getObject()));
    }

    private static void onAttachLevelCapability(AttachCapabilitiesEvent<Level> event) {
        // Attaches the level capabilities
        levelCapAttachers.forEach(attacher -> attacher.accept(event, event.getObject()));
    }

    private static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Syncs a player's capabilities to themselves on world join (either joining server or switching worlds)
            entityCapRetrievers.forEach(capRetriever -> capRetriever.apply(player).ifPresent(cap -> cap.sendUpdatePacketToPlayer(player)));

            // Sync level capabilities to a player when they join that level
            levelCapRetrievers.forEach(capRetriever -> capRetriever.apply(player.level).ifPresent(cap -> cap.sendUpdatePacketToPlayer(player)));
        }
    }

    private static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        // Syncs an entity's capabilities to a player when they start tracking it
        ServerPlayer currentPlayer = (ServerPlayer) event.getPlayer();
        entityCapRetrievers.forEach(capRetriever -> capRetriever.apply(event.getTarget()).ifPresent(cap -> cap.sendUpdatePacketToPlayer(currentPlayer)));
    }

    private static void onPlayerClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getPlayer();

        // Revive the old player's capabilities; so we can copy them over to the new player
        oldPlayer.reviveCaps();

        playerCapCloners.forEach(capCloner -> capCloner.accept(oldPlayer, newPlayer));
    }
}
