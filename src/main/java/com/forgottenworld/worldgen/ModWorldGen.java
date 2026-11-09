package com.forgottenworld.worldgen;

import com.forgottenworld.ForgottenWorld;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Ether ore generation is data-driven (see data/forgottenworld/worldgen/*).
 * Here we only attach the placed feature to all Overworld biomes.
 */
public final class ModWorldGen {
    public static final ResourceKey<PlacedFeature> ETHER_ORE_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE, ForgottenWorld.id("ether_ore"));

    private ModWorldGen() {
    }

    public static void register() {
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ETHER_ORE_PLACED);
    }
}
