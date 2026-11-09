package com.forgottenworld.dimension;

import com.forgottenworld.ForgottenWorld;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public final class ModDimensions {
    public static final ResourceKey<Level> FORGOTTEN_LEVEL =
            ResourceKey.create(Registries.DIMENSION, ForgottenWorld.id("forgotten"));

    public static final ResourceKey<DimensionType> FORGOTTEN_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE, ForgottenWorld.id("forgotten"));

    private ModDimensions() {
    }

    public static void register() {
        // Data-driven dimension; keys are referenced by portal teleportation.
    }
}
