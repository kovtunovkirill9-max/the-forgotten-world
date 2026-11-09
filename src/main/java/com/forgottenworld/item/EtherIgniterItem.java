package com.forgottenworld.item;

import com.forgottenworld.block.ForgottenPortalBlock;
import com.forgottenworld.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class EtherIgniterItem extends Item {
    public EtherIgniterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!state.is(ModBlocks.FORGOTTEN_FRAME)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        BlockPos inside = pos.relative(context.getClickedFace());
        Optional<PortalFrame> frame = PortalFrame.find(level, inside);
        if (frame.isEmpty()) {
            return InteractionResult.FAIL;
        }
        frame.get().placePortalBlocks(level);
        level.playSound(null, inside, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 0.8F);
        if (context.getPlayer() != null) {
            context.getItemInHand().hurtAndBreak(1, context.getPlayer(),
                    context.getHand() == net.minecraft.world.InteractionHand.MAIN_HAND
                            ? net.minecraft.world.entity.EquipmentSlot.MAINHAND
                            : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
        }
        return InteractionResult.SUCCESS;
    }

    private record PortalFrame(BlockPos bottomLeft, int width, int height, Direction.Axis axis) {
        static Optional<PortalFrame> find(Level level, BlockPos start) {
            for (Direction.Axis axis : new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}) {
                Optional<PortalFrame> found = findAxis(level, start, axis);
                if (found.isPresent()) return found;
            }
            return Optional.empty();
        }

        static Optional<PortalFrame> findAxis(Level level, BlockPos start, Direction.Axis axis) {
            Direction right = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
            BlockPos left = start;
            while (isEmpty(level, left.relative(right.getOpposite()))) left = left.relative(right.getOpposite());
            BlockPos bottom = left;
            while (isEmpty(level, bottom.below())) bottom = bottom.below();
            int width = 0;
            BlockPos cursor = bottom;
            while (isEmpty(level, cursor)) { width++; cursor = cursor.relative(right); if (width > 21) return Optional.empty(); }
            int height = 0;
            cursor = bottom;
            while (isEmpty(level, cursor)) { height++; cursor = cursor.above(); if (height > 21) return Optional.empty(); }
            if (width < 2 || height < 3) return Optional.empty();
            if (!isValid(level, bottom, width, height, right)) return Optional.empty();
            return Optional.of(new PortalFrame(bottom, width, height, axis));
        }

        static boolean isEmpty(Level level, BlockPos pos) {
            BlockState s = level.getBlockState(pos);
            return s.isAir() || s.is(ModBlocks.FORGOTTEN_PORTAL);
        }

        static boolean isFrame(Level level, BlockPos pos) {
            return level.getBlockState(pos).is(ModBlocks.FORGOTTEN_FRAME);
        }

        static boolean isValid(Level level, BlockPos bottomLeft, int width, int height, Direction right) {
            for (int x = -1; x <= width; x++) {
                for (int y = -1; y <= height; y++) {
                    BlockPos p = bottomLeft.relative(right, x).above(y);
                    boolean border = x == -1 || x == width || y == -1 || y == height;
                    if (border) {
                        if (!isFrame(level, p)) return false;
                    } else {
                        if (!isEmpty(level, p)) return false;
                    }
                }
            }
            return true;
        }

        void placePortalBlocks(Level level) {
            Direction right = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
            BlockState portal = ModBlocks.FORGOTTEN_PORTAL.defaultBlockState().setValue(ForgottenPortalBlock.AXIS, axis);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    level.setBlock(bottomLeft.relative(right, x).above(y), portal, 2);
                }
            }
        }
    }
}
