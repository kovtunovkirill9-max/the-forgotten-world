package com.forgottenworld.block;

import com.forgottenworld.dimension.ModDimensions;
import com.forgottenworld.dimension.PortalLinkData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;

public class ForgottenPortalBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    protected static final VoxelShape X_AXIS_AABB = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
    private static final int SEARCH_RADIUS = 128;

    public ForgottenPortalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(AXIS) == Direction.Axis.Z ? Z_AXIS_AABB : X_AXIS_AABB;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
                                InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (level.isClientSide() || !entity.canUsePortal(false)) {
            return;
        }
        if (entity.getPortalCooldown() > 0) {
            entity.setPortalCooldown();
            return;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        ResourceKey<Level> current = level.dimension();
        ResourceKey<Level> targetKey = current.equals(ModDimensions.FORGOTTEN_LEVEL)
                ? Level.OVERWORLD : ModDimensions.FORGOTTEN_LEVEL;
        if (targetKey.equals(current)) {
            return;
        }

        ServerLevel targetLevel = serverLevel.getServer().getLevel(targetKey);
        if (targetLevel == null) {
            return;
        }

        Direction.Axis axis = state.getValue(AXIS);
        ServerLevel overworld = serverLevel.getServer().getLevel(Level.OVERWORLD);
        PortalLinkData linkData = overworld != null ? PortalLinkData.get(overworld) : null;

        BlockPos linked;
        if (linkData != null && entity.getUUID() != null) {
            var remembered = linkData.recall(entity.getUUID(), current, pos);
            if (remembered.isPresent() && targetLevel.getBlockState(remembered.get()).is(ModBlocks.FORGOTTEN_PORTAL)) {
                linked = remembered.get();
            } else if (remembered.isPresent() && !targetLevel.getBlockState(remembered.get()).is(ModBlocks.FORGOTTEN_PORTAL)) {
                // Source/target portal mapping exists but portal was destroyed; recreate safely near remembered spot.
                linked = createPortalNear(targetLevel, remembered.get(), axis);
                linkData.remember(entity.getUUID(), current, pos, linked);
            } else {
                linked = findOrCreateLinkedPortal(targetLevel, entity.blockPosition(), axis);
                linkData.remember(entity.getUUID(), current, pos, linked);
                // Also remember reverse mapping for return after restart.
                linkData.remember(entity.getUUID(), targetKey, linked, pos);
            }
        } else {
            linked = findOrCreateLinkedPortal(targetLevel, entity.blockPosition(), axis);
        }

        Vec3 dest = Vec3.atBottomCenterOf(findStandNearPortal(targetLevel, linked));
        entity.setPortalCooldown();
        TeleportTransition transition = new TeleportTransition(
                targetLevel, dest, Vec3.ZERO, entity.getYRot(), entity.getXRot(),
                TeleportTransition.DO_NOTHING);
        Entity teleported = entity.teleport(transition);
        if (teleported != null) {
            teleported.setPortalCooldown();
            targetLevel.playSound(null, linked, SoundEvents.PORTAL_TRAVEL, SoundSource.BLOCKS, 0.5F, 0.8F);
        }
    }

    private static BlockPos findOrCreateLinkedPortal(ServerLevel level, BlockPos around, Direction.Axis axis) {
        Optional<BlockPos> existing = findNearbyPortal(level, around, SEARCH_RADIUS);
        if (existing.isPresent()) {
            return existing.get();
        }
        BlockPos safe = findSafeBase(level, around);
        createPortal(level, safe, axis);
        return safe.above();
    }

    private static BlockPos createPortalNear(ServerLevel level, BlockPos around, Direction.Axis axis) {
        BlockPos safe = findSafeBase(level, around);
        createPortal(level, safe, axis);
        return safe.above();
    }

    private static BlockPos findStandNearPortal(ServerLevel level, BlockPos portalPos) {
        if (level.getBlockState(portalPos).is(ModBlocks.FORGOTTEN_PORTAL)) {
            return findPortalStand(level, portalPos);
        }
        if (canStand(level, portalPos)) return portalPos;
        BlockPos safe = findSafeBase(level, portalPos);
        return safe.above();
    }

    private static Optional<BlockPos> findNearbyPortal(ServerLevel level, BlockPos around, int radius) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int minY = level.dimensionType().minY();
        int maxY = minY + level.dimensionType().height() - 1;
        int y0 = Math.max(minY + 1, around.getY() - 16);
        int y1 = Math.min(maxY - 2, around.getY() + 16);
        int step = 2; // performance-friendly scan
        for (int r = 0; r <= radius; r += step) {
            for (int dx = -r; dx <= r; dx += Math.max(step, r == 0 ? 1 : r * 2)) {
                for (int dz = -r; dz <= r; dz += step) {
                    if (Math.abs(dx) != r && Math.abs(dz) != r && r != 0) continue;
                    for (int y = y0; y <= y1; y += 2) {
                        cursor.set(around.getX() + dx, y, around.getZ() + dz);
                        if (level.getBlockState(cursor).is(ModBlocks.FORGOTTEN_PORTAL)) {
                            return Optional.of(findPortalStand(level, cursor));
                        }
                    }
                }
            }
        }
        // fallback full-ish local scan
        for (int dx = -32; dx <= 32; dx += 2) {
            for (int dz = -32; dz <= 32; dz += 2) {
                for (int y = y0; y <= y1; y++) {
                    cursor.set(around.getX() + dx, y, around.getZ() + dz);
                    if (level.getBlockState(cursor).is(ModBlocks.FORGOTTEN_PORTAL)) {
                        return Optional.of(findPortalStand(level, cursor));
                    }
                }
            }
        }
        return Optional.empty();
    }

    private static BlockPos findPortalStand(ServerLevel level, BlockPos portalPos) {
        BlockPos.MutableBlockPos m = portalPos.mutable();
        while (level.getBlockState(m).is(ModBlocks.FORGOTTEN_PORTAL)) m.move(Direction.DOWN);
        BlockPos base = m.immutable();
        if (canStand(level, base.above())) return base.above();
        for (Direction d : Direction.Plane.HORIZONTAL) {
            BlockPos p = base.above().relative(d);
            if (canStand(level, p) || level.getBlockState(p).getCollisionShape(level, p).isEmpty()) return p;
        }
        return base.above();
    }

    private static BlockPos findSafeBase(ServerLevel level, BlockPos around) {
        int minY = level.dimensionType().minY() + 1;
        int maxY = minY + level.dimensionType().height() - 5;
        int baseY = Math.max(minY + 1, Math.min(around.getY(), maxY - 4));
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dy = 0; dy < 48; dy++) {
            for (int sign : new int[]{1, -1}) {
                int y = baseY + dy * sign;
                if (y <= minY || y >= maxY) continue;
                for (int dx = -4; dx <= 4; dx++) {
                    for (int dz = -4; dz <= 4; dz++) {
                        cursor.set(around.getX() + dx, y, around.getZ() + dz);
                        if (isBuildableArea(level, cursor)) {
                            return cursor.immutable();
                        }
                    }
                }
            }
        }
        BlockPos fallback = new BlockPos(around.getX(), Math.max(80, minY + 10), around.getZ());
        clearArea(level, fallback);
        return fallback;
    }

    private static boolean isBuildableArea(ServerLevel level, BlockPos base) {
        // Need space for 4 wide x 5 high frame footprint
        for (int x = -1; x <= 2; x++) {
            for (int y = 0; y <= 4; y++) {
                BlockPos p = base.offset(x, y, 0);
                BlockState s = level.getBlockState(p);
                if (y == 0) {
                    if (s.getCollisionShape(level, p).isEmpty() && !s.is(Blocks.BEDROCK)) continue; // will place frame below/ground ok
                } else {
                    if (!s.canBeReplaced()) return false;
                }
            }
        }
        return true;
    }

    private static void clearArea(ServerLevel level, BlockPos base) {
        for (int x = -1; x <= 2; x++) {
            for (int y = 0; y <= 4; y++) {
                BlockPos p = base.offset(x, y, 0);
                if (y > 0) level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
            }
        }
    }

    private static void createPortal(ServerLevel level, BlockPos base, Direction.Axis axis) {
        Direction right = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        BlockState frame = ModBlocks.FORGOTTEN_FRAME.defaultBlockState();
        BlockState portal = ModBlocks.FORGOTTEN_PORTAL.defaultBlockState().setValue(AXIS, axis);
        // 4x5 frame (2x3 interior)
        for (int x = -1; x <= 2; x++) {
            for (int y = 0; y <= 4; y++) {
                BlockPos p = base.relative(right, x).above(y);
                boolean border = x == -1 || x == 2 || y == 0 || y == 4;
                level.setBlock(p, border ? frame : portal, 2);
            }
        }
        // ensure ground
        for (int x = -1; x <= 2; x++) {
            BlockPos below = base.relative(right, x).below();
            if (level.getBlockState(below).isAir()) {
                level.setBlock(below, frame, 2);
            }
        }
    }

    private static boolean canStand(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()
                && !level.getBlockState(pos.below()).getCollisionShape(level, pos.below()).isEmpty();
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(100) == 0) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F, random.nextFloat() * 0.4F + 0.8F, false);
        }
        for (int i = 0; i < 2; ++i) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.PORTAL, x, y, z,
                    (random.nextDouble() - 0.5) * 0.5, (random.nextDouble() - 0.5) * 0.5, (random.nextDouble() - 0.5) * 0.5);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }
}
