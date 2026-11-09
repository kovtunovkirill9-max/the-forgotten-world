package com.forgottenworld.block;

import com.forgottenworld.ForgottenWorld;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class ModBlocks {
    public static final Block ETHER_ORE = registerWithItem("ether_ore", key ->
            new DropExperienceBlock(
                    UniformInt.of(1, 4),
                    BlockBehaviour.Properties.of()
                            .setId(key)
                            .mapColor(MapColor.STONE)
                            .strength(3.0F, 3.0F)
                            .requiresCorrectToolForDrops()
                            .lightLevel(state -> 3)
                            .sound(SoundType.AMETHYST)));

    private ModBlocks() {
    }

    private static Block registerWithItem(String name, java.util.function.Function<ResourceKey<Block>, Block> factory) {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, ForgottenWorld.id(name));
        Block block = Registry.register(BuiltInRegistries.BLOCK, blockKey, factory.apply(blockKey));

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, ForgottenWorld.id(name));
        Registry.register(BuiltInRegistries.ITEM, itemKey,
                new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));
        return block;
    }

    /** Forces class loading so registration happens during mod initialization. */
    public static void register() {
    }
}
