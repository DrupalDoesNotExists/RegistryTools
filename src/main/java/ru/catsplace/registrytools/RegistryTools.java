package ru.catsplace.registrytools;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import com.mojang.serialization.Lifecycle;

import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.ExecutionException;

/**
 * RegistryTools is a simple API wrapper around NMS registry.
 *
 * Licensed under MIT.
 *
 * @author DrupalDoesNotExists
 */
public final class RegistryTools {

    /*
    Instance owner. Used to generate namespaces strictly & automatically.
     */
    private final JavaPlugin host;

    /*
    Cache size
     */
    private int cacheSize;

    /*
    DedicatedServer cache
     */
    private static final DedicatedServer dedicatedServer = ((CraftServer) Bukkit.getServer()).getServer();

    /*
    Var handle cache
     */
    private static VarHandle handle;

    /*
    Registry instances cache
     */
    private final LoadingCache<ResourceKey<? extends Registry<?>>, WritableRegistry<?>> registryCache =
                CacheBuilder.newBuilder().weakKeys().softValues()
                .maximumSize(cacheSize).build(new CacheLoader<>() {
                    @Override
                    public WritableRegistry<?> load(ResourceKey<? extends Registry<?>> key) {
                        return (WritableRegistry<?>) dedicatedServer.registryAccess().ownedRegistryOrThrow(key);
                    }
                });

    public RegistryTools(JavaPlugin host) { this.host = host; this.cacheSize = 20; }
    public RegistryTools(JavaPlugin host, int cacheSize) { this.host = host; this.cacheSize = cacheSize; }

    /**
     * Return registry from cache
     * @param key Key
     * @param <T> Entry type
     * @throws ExecutionException when trying to load registry to cache
     * @return Registry
     */
    public <T> @NotNull Registry<T> cachedRegistry(ResourceKey<Registry<T>> key) throws ExecutionException {
        return (Registry<T>) registryCache.get(key);
    }

    /**
     * Return registry from cache if present
     * @param key Key
     * @param <T> Entry type
     * @return Registry or null
     */
    public <T> @Nullable Registry<T> cachedRegistryIfPresent(ResourceKey<Registry<T>> key) {
        return (Registry<T>) registryCache.getIfPresent(key);
    }

    /**
     * Create ResourceKey instance
     * @param registry Registry-host
     * @param id       Id
     * @param <T>      Entry type
     * @return ResourceKey
     */
    public <T> @NotNull ResourceKey<T> key(Registry<T> registry, String id) {
        return ResourceKey.create(
                registry.key(),
                new ResourceLocation(
                        host.getName(),
                        id
                )
        );
    }

    /**
     * Register entry to registry
     * @param registry Registry
     * @param key      Key
     * @param object   Entry
     * @param <T>      Entry type
     * @return Holder
     */
    public <T> @NotNull Holder<T> register(WritableRegistry<T> registry, ResourceKey<T> key, T object) {
        return registry.register(key, object, Lifecycle.stable());
    }

    /**
     * Register entry to registry
     * @param registry Registry
     * @param id       Id
     * @param object   Entry
     * @param <T>      Entry type
     * @return Holder
     */
    public <T> @NotNull Holder<T> register(WritableRegistry<T> registry, String id, T object) { return register(registry, key(registry, id), object); }

    /**
     * Freeze/unfreeze registry
     * @param registry Registry
     * @param value    Value
     * @param <T>      Entry type
     */
    public <T> void freeze(Registry<T> registry, boolean value) throws IllegalAccessException, NoSuchFieldException {

        if (handle == null) {
            handle = MethodHandles.privateLookupIn(
                    MappedRegistry.class,
                    MethodHandles.lookup()
            ).findVarHandle(MappedRegistry.class, "ca", boolean.class); // "ca" = frozen
        }

        handle.set(registry, value);

    }

}
