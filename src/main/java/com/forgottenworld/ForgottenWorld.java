package com.forgottenworld;

import com.forgottenworld.block.ModBlocks;
import com.forgottenworld.dimension.ModDimensions;
import com.forgottenworld.item.ModItems;
import com.forgottenworld.worldgen.ModWorldGen;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Forgotten World — 0.2.0 "Beyond the Veil".
 * Adds a purple-black portal to the Forgotten dimension.
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
        ModDimensions.register();
        LOGGER.info("The Forgotten World {} — Beyond the Veil loaded.", "0.2.0");
    }
}
