package com.forgottenworld.entity;

import com.forgottenworld.ForgottenWorld;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModEntities {
    public static final ResourceKey<EntityType<?>> SHADOW_WISP_KEY = key("shadow_wisp");
    public static final ResourceKey<EntityType<?>> FORGOTTEN_LURKER_KEY = key("forgotten_lurker");
    public static final ResourceKey<EntityType<?>> UMBRAL_GOLEM_KEY = key("umbral_golem");
    public static final ResourceKey<EntityType<?>> VEIL_WATCHER_KEY = key("veil_watcher");

    public static final EntityType<ShadowWispEntity> SHADOW_WISP = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, SHADOW_WISP_KEY,
            EntityType.Builder.of(ShadowWispEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.2F).eyeHeight(0.9F)
                    .build(SHADOW_WISP_KEY));

    public static final EntityType<ForgottenLurkerEntity> FORGOTTEN_LURKER = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, FORGOTTEN_LURKER_KEY,
            EntityType.Builder.of(ForgottenLurkerEntity::new, MobCategory.MONSTER)
                    .sized(0.7F, 1.9F).eyeHeight(1.6F)
                    .build(FORGOTTEN_LURKER_KEY));

    public static final EntityType<UmbralGolemEntity> UMBRAL_GOLEM = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, UMBRAL_GOLEM_KEY,
            EntityType.Builder.of(UmbralGolemEntity::new, MobCategory.MONSTER)
                    .sized(1.1F, 2.6F).eyeHeight(2.2F)
                    .build(UMBRAL_GOLEM_KEY));

    public static final EntityType<VeilWatcherEntity> VEIL_WATCHER = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, VEIL_WATCHER_KEY,
            EntityType.Builder.of(VeilWatcherEntity::new, MobCategory.MONSTER)
                    .sized(1.4F, 3.2F).eyeHeight(2.7F)
                    .build(VEIL_WATCHER_KEY));

    private static ResourceKey<EntityType<?>> key(String name) {
        return ResourceKey.create(Registries.ENTITY_TYPE, ForgottenWorld.id(name));
    }

    public static void register() {
        FabricDefaultAttributeRegistry.register(SHADOW_WISP, ShadowWispEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(FORGOTTEN_LURKER, ForgottenLurkerEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(UMBRAL_GOLEM, UmbralGolemEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(VEIL_WATCHER, VeilWatcherEntity.createAttributes());
    }

    private ModEntities() {
    }
}
