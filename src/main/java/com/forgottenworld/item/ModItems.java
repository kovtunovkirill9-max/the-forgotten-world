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
    public static final Item ETHER_SHARD = register("ether_shard", Item::new,
            new Item.Properties());

    public static final Item VOID_STAFF = register("void_staff", VoidStaffItem::new,
            new Item.Properties()
                    .stacksTo(1)
                    .durability(VoidStaffItem.DURABILITY)
                    .rarity(Rarity.RARE));

    public static final Item ETHER_IGNITER = register("ether_igniter", EtherIgniterItem::new,
            new Item.Properties()
                    .stacksTo(1)
                    .durability(64)
                    .rarity(Rarity.UNCOMMON));

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
