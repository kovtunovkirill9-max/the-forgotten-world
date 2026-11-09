package com.forgottenworld.dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lightweight portal link helper. Spatial portal search/creation in ForgottenPortalBlock
 * provides restart-safe return behavior because portal blocks persist in the world.
 */
public final class PortalLinkData {
    private static final Map<String, BlockPos> LINKS = new ConcurrentHashMap<>();

    private PortalLinkData() {
    }

    public static PortalLinkData get(ServerLevel level) {
        return new PortalLinkData();
    }

    private static String key(UUID player, ResourceKey<Level> fromDim, BlockPos fromArea) {
        int ax = Math.floorDiv(fromArea.getX(), 16);
        int az = Math.floorDiv(fromArea.getZ(), 16);
        return player + "|" + fromDim + "|" + ax + "," + az;
    }

    public void remember(UUID player, ResourceKey<Level> fromDim, BlockPos fromArea, BlockPos targetPortalPos) {
        LINKS.put(key(player, fromDim, fromArea), targetPortalPos.immutable());
    }

    public Optional<BlockPos> recall(UUID player, ResourceKey<Level> fromDim, BlockPos fromArea) {
        return Optional.ofNullable(LINKS.get(key(player, fromDim, fromArea)));
    }
}
