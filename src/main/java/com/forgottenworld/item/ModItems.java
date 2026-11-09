package com.forgottenworld.item;

import com.forgottenworld.ForgottenWorld;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;

public final class ModItems {
    // Resources
    public static final Item ETHER_SHARD = register("ether_shard", Item::new, new Item.Properties());
    public static final Item UMBRAL_SHARD = register("umbral_shard", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item GLOOM_CRYSTAL = register("gloom_crystal", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item UMBRAL_INGOT = register("umbral_ingot", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item SHADOW_DUST = register("shadow_dust", Item::new, new Item.Properties());

    // Utility (preserved)
    public static final Item VOID_STAFF = register("void_staff", VoidStaffItem::new,
            new Item.Properties().stacksTo(1).durability(VoidStaffItem.DURABILITY).rarity(Rarity.RARE));
    public static final Item ETHER_IGNITER = register("ether_igniter", EtherIgniterItem::new,
            new Item.Properties().stacksTo(1).durability(64).rarity(Rarity.UNCOMMON));
    public static final Item DIMENSION_KEY = register("dimension_key", DimensionKeyItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    // Weapons & tools
    public static final Item UMBRAL_SWORD = register("umbral_sword", UmbralSwordItem::new,
            new Item.Properties().stacksTo(1).durability(900).rarity(Rarity.UNCOMMON));
    public static final Item UMBRAL_PICKAXE = register("umbral_pickaxe", UmbralPickaxeItem::new,
            new Item.Properties().stacksTo(1).durability(900).rarity(Rarity.UNCOMMON));
    public static final Item UMBRAL_AXE = register("umbral_axe", UmbralAxeItem::new,
            new Item.Properties().stacksTo(1).durability(900).rarity(Rarity.UNCOMMON));
    public static final Item UMBRAL_SHOVEL = register("umbral_shovel", UmbralShovelItem::new,
            new Item.Properties().stacksTo(1).durability(900).rarity(Rarity.UNCOMMON));
    public static final Item UMBRAL_HOE = register("umbral_hoe", UmbralHoeItem::new,
            new Item.Properties().stacksTo(1).durability(900).rarity(Rarity.UNCOMMON));

    // Armor
    public static final Item UMBRAL_HELMET = register("umbral_helmet", Item::new,
            new Item.Properties().stacksTo(1).durability(420).rarity(Rarity.UNCOMMON));
    public static final Item UMBRAL_CHESTPLATE = register("umbral_chestplate", Item::new,
            new Item.Properties().stacksTo(1).durability(620).rarity(Rarity.UNCOMMON));
    public static final Item UMBRAL_LEGGINGS = register("umbral_leggings", Item::new,
            new Item.Properties().stacksTo(1).durability(580).rarity(Rarity.UNCOMMON));
    public static final Item UMBRAL_BOOTS = register("umbral_boots", Item::new,
            new Item.Properties().stacksTo(1).durability(500).rarity(Rarity.UNCOMMON));

    private ModItems() {
    }

    private static <T extends Item> T register(String name, Function<Item.Properties, T> factory, Item.Properties properties) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ForgottenWorld.id(name));
        T item = factory.apply(properties.setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void register() {
        ModItemGroups.register();
    }
}
