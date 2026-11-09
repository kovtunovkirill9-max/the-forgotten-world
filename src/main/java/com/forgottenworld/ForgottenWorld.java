package com.forgottenworld;

import com.forgottenworld.block.ModBlocks;
import com.forgottenworld.item.ModItems;
import com.forgottenworld.worldgen.ModWorldGen;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Forgotten World — 0.1 "First Awakening".
 * Server-side logic only: no client entrypoint, no custom renderers, no extra libraries.
 */
public class ForgottenWorld implements ModInitializer {
    public static final String MOD_ID = "forgottenworld";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModItems.register();
        ModWorldGen.register();
        LOGGER.info("The Forgotten World 0.1 — First Awakening loaded.");
    }
}
