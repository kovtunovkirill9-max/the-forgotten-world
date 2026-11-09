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
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Function;

public final class ModBlocks {
    public static final Block ETHER_ORE = registerWithItem("ether_ore", key ->
            new DropExperienceBlock(UniformInt.of(1, 4), ore(key, MapColor.STONE, 3.0F, 3.0F).lightLevel(s -> 3).sound(SoundType.AMETHYST)));

    public static final Block UMBRAL_ORE = registerWithItem("umbral_ore", key ->
            new DropExperienceBlock(UniformInt.of(2, 5), ore(key, MapColor.DEEPSLATE, 4.0F, 4.0F).lightLevel(s -> 4).sound(SoundType.DEEPSLATE)));

    public static final Block GLOOM_ORE = registerWithItem("gloom_ore", key ->
            new DropExperienceBlock(UniformInt.of(2, 4), ore(key, MapColor.STONE, 3.5F, 3.5F).lightLevel(s -> 2).sound(SoundType.STONE)));

    public static final Block FORGOTTEN_FRAME = registerWithItem("forgotten_frame", key ->
            new Block(BlockBehaviour.Properties.of().setId(key).mapColor(MapColor.COLOR_PURPLE).strength(4.0F, 1200.0F)
                    .requiresCorrectToolForDrops().lightLevel(s -> 5).sound(SoundType.STONE)));

    public static final Block FORGOTTEN_PORTAL = registerNoItem("forgotten_portal", key ->
            new ForgottenPortalBlock(BlockBehaviour.Properties.of().setId(key).mapColor(MapColor.COLOR_BLACK).noCollision()
                    .randomTicks().strength(-1.0F).sound(SoundType.GLASS).lightLevel(s -> 11).pushReaction(PushReaction.BLOCK).noLootTable()));

    public static final Block FORGOTTEN_STONE = registerWithItem("forgotten_stone", key ->
            new Block(stoneLike(key, MapColor.COLOR_PURPLE, 2.5F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()));

    public static final Block FORGOTTEN_STONE_BRICKS = registerWithItem("forgotten_stone_bricks", key ->
            new Block(stoneLike(key, MapColor.COLOR_PURPLE, 2.5F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()));

    public static final Block GLOOM_DIRT = registerWithItem("gloom_dirt", key ->
            new Block(BlockBehaviour.Properties.of().setId(key).mapColor(MapColor.COLOR_PURPLE).strength(0.6F).sound(SoundType.GRAVEL)));

    public static final Block UMBRA_LOG = registerWithItem("umbra_log", key ->
            new RotatedPillarBlock(BlockBehaviour.Properties.of().setId(key).mapColor(MapColor.COLOR_PURPLE).strength(2.0F).sound(SoundType.WOOD)));

    public static final Block UMBRA_PLANKS = registerWithItem("umbra_planks", key ->
            new Block(BlockBehaviour.Properties.of().setId(key).mapColor(MapColor.COLOR_PURPLE).strength(2.0F, 3.0F).sound(SoundType.WOOD)));

    public static final Block UMBRA_LEAVES = registerWithItem("umbra_leaves", key ->
            new Block(BlockBehaviour.Properties.of().setId(key).mapColor(MapColor.COLOR_PURPLE).strength(0.2F)
                    .sound(SoundType.GRASS).noOcclusion().isSuffocating((s, l, p) -> false).isViewBlocking((s, l, p) -> false)));

    public static final Block GLOW_MOSS = registerWithItem("glow_moss", key ->
            new Block(BlockBehaviour.Properties.of().setId(key).mapColor(MapColor.COLOR_PURPLE).strength(0.2F)
                    .sound(SoundType.MOSS).lightLevel(s -> 7)));

    public static final Block ETHER_LANTERN = registerWithItem("ether_lantern", key ->
            new Block(BlockBehaviour.Properties.of().setId(key).mapColor(MapColor.COLOR_PURPLE).strength(2.0F, 6.0F)
                    .sound(SoundType.LANTERN).lightLevel(s -> 14)));

    private ModBlocks() {
    }

    private static BlockBehaviour.Properties ore(ResourceKey<Block> key, MapColor color, float strength, float resistance) {
        return BlockBehaviour.Properties.of().setId(key).mapColor(color).strength(strength, resistance).requiresCorrectToolForDrops();
    }

    private static BlockBehaviour.Properties stoneLike(ResourceKey<Block> key, MapColor color, float strength, float resistance) {
        return BlockBehaviour.Properties.of().setId(key).mapColor(color).strength(strength, resistance);
    }

    private static Block registerWithItem(String name, Function<ResourceKey<Block>, Block> factory) {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, ForgottenWorld.id(name));
        Block block = Registry.register(BuiltInRegistries.BLOCK, blockKey, factory.apply(blockKey));
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, ForgottenWorld.id(name));
        Registry.register(BuiltInRegistries.ITEM, itemKey,
                new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));
        return block;
    }

    private static Block registerNoItem(String name, Function<ResourceKey<Block>, Block> factory) {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, ForgottenWorld.id(name));
        return Registry.register(BuiltInRegistries.BLOCK, blockKey, factory.apply(blockKey));
    }

    public static void register() {
    }
}
