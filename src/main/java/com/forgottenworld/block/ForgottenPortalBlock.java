package com.forgottenworld.block;

import com.forgottenworld.dimension.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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

public class ForgottenPortalBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    protected static final VoxelShape X_AXIS_AABB = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

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
        ResourceKey<Level> targetKey = level.dimension() == ModDimensions.FORGOTTEN_LEVEL
                ? Level.OVERWORLD : ModDimensions.FORGOTTEN_LEVEL;
        ServerLevel targetLevel = serverLevel.getServer().getLevel(targetKey);
        if (targetLevel == null) {
            return;
        }

        BlockPos targetPos = findSafePos(targetLevel, entity.blockPosition());
        Vec3 dest = Vec3.atBottomCenterOf(targetPos);
        entity.setPortalCooldown();

        TeleportTransition transition = new TeleportTransition(
                targetLevel, dest, Vec3.ZERO, entity.getYRot(), entity.getXRot(),
                TeleportTransition.DO_NOTHING);
        Entity teleported = entity.teleport(transition);
        if (teleported != null) {
            teleported.setPortalCooldown();
            targetLevel.playSound(null, targetPos, SoundEvents.PORTAL_TRAVEL, SoundSource.BLOCKS, 0.5F, 0.8F);
        }
    }

    private static BlockPos findSafePos(ServerLevel level, BlockPos around) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int minY = level.dimensionType().minY();
        int maxY = minY + level.dimensionType().height();
        int baseY = Math.max(minY + 1, Math.min(around.getY(), maxY - 3));
        for (int dy = 0; dy < 32; dy++) {
            for (int sign : new int[]{1, -1}) {
                int y = baseY + dy * sign;
                if (y <= minY || y >= maxY - 2) continue;
                cursor.set(around.getX(), y, around.getZ());
                if (canStand(level, cursor)) {
                    ensurePlatform(level, cursor);
                    return cursor.immutable();
                }
            }
        }
        BlockPos fallback = new BlockPos(around.getX(), Math.max(baseY, 80), around.getZ());
        ensurePlatform(level, fallback);
        return fallback;
    }

    private static boolean canStand(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()
                && !level.getBlockState(pos.below()).getCollisionShape(level, pos.below()).isEmpty();
    }

    private static void ensurePlatform(ServerLevel level, BlockPos feet) {
        BlockPos below = feet.below();
        if (level.getBlockState(below).isAir()) {
            level.setBlock(below, ModBlocks.FORGOTTEN_FRAME.defaultBlockState(), Block.UPDATE_ALL);
        }
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
