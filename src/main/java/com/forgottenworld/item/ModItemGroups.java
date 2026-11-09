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
                            entries.accept(ModBlocks.ETHER_ORE);
                            entries.accept(ModBlocks.FORGOTTEN_FRAME);
                            entries.accept(ModItems.ETHER_SHARD);
                            entries.accept(ModItems.ETHER_IGNITER);
                            entries.accept(ModItems.VOID_STAFF);
                        })
                        .build());
    }

    private ModItemGroups() {
    }
}
