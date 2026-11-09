package com.forgottenworld.item;

import com.forgottenworld.ForgottenWorld;
import com.forgottenworld.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModItemGroups {
    public static final ResourceKey<CreativeModeTab> FORGOTTEN_WORLD_TAB = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB, ForgottenWorld.id("forgotten_world"));

    public static void register() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, FORGOTTEN_WORLD_TAB,
                FabricItemGroup.builder()
                        .title(Component.translatable("itemGroup.forgottenworld.forgotten_world"))
                        .icon(() -> new ItemStack(ModItems.ETHER_IGNITER))
                        .displayItems((context, entries) -> {
                            // Portal & dimension
                            entries.accept(ModBlocks.FORGOTTEN_FRAME);
                            entries.accept(ModItems.ETHER_IGNITER);
                            entries.accept(ModItems.DIMENSION_KEY);
                            entries.accept(ModItems.VOID_STAFF);
                            // Ores & resources
                            entries.accept(ModBlocks.ETHER_ORE);
                            entries.accept(ModBlocks.UMBRAL_ORE);
                            entries.accept(ModBlocks.GLOOM_ORE);
                            entries.accept(ModItems.ETHER_SHARD);
                            entries.accept(ModItems.UMBRAL_SHARD);
                            entries.accept(ModItems.GLOOM_CRYSTAL);
                            entries.accept(ModItems.UMBRAL_INGOT);
                            entries.accept(ModItems.SHADOW_DUST);
                            // Weapons & tools
                            entries.accept(ModItems.UMBRAL_SWORD);
                            entries.accept(ModItems.UMBRAL_PICKAXE);
                            entries.accept(ModItems.UMBRAL_AXE);
                            entries.accept(ModItems.UMBRAL_SHOVEL);
                            entries.accept(ModItems.UMBRAL_HOE);
                            // Armor
                            entries.accept(ModItems.UMBRAL_HELMET);
                            entries.accept(ModItems.UMBRAL_CHESTPLATE);
                            entries.accept(ModItems.UMBRAL_LEGGINGS);
                            entries.accept(ModItems.UMBRAL_BOOTS);
                            // Dimension blocks & decor
                            entries.accept(ModBlocks.FORGOTTEN_STONE);
                            entries.accept(ModBlocks.FORGOTTEN_STONE_BRICKS);
                            entries.accept(ModBlocks.GLOOM_DIRT);
                            entries.accept(ModBlocks.UMBRA_LOG);
                            entries.accept(ModBlocks.UMBRA_PLANKS);
                            entries.accept(ModBlocks.UMBRA_LEAVES);
                            entries.accept(ModBlocks.GLOW_MOSS);
                            entries.accept(ModBlocks.ETHER_LANTERN);
                        })
                        .build());
    }

    private ModItemGroups() {
    }
}
