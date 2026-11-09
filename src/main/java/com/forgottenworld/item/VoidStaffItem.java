package com.forgottenworld.item;

import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

/**
 * Void Staff: right-click to blink forward up to {@link #RANGE} blocks along your line of sight.
 * You stop before walls, never end inside blocks, take no fall damage from the jump,
 * and the staff has a cooldown and loses durability on each use.
 */
public class VoidStaffItem extends Item {
    public static final int DURABILITY = 200;
    public static final double RANGE = 12.0;
    public static final int COOLDOWN_TICKS = 60;

    public VoidStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            // Client side: report success so the hand swings; the server does the real work.
            return InteractionResult.SUCCESS;
        }

        ItemStack stack = player.getItemInHand(hand);

        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = eye.add(look.scale(RANGE));

        BlockHitResult hit = serverLevel.clip(new ClipContext(
                eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

        double maxDistance = hit.getType() == HitResult.Type.MISS
                ? RANGE
                : Math.max(0.0, eye.distanceTo(hit.getLocation()) - 0.5);

        // Walk back from the farthest point until the player's hitbox fits without collisions.
        Vec3 destination = null;
        for (double d = maxDistance; d >= 1.0; d -= 0.25) {
            Vec3 feet = eye.add(look.scale(d)).subtract(0.0, player.getEyeHeight(), 0.0);
            AABB moved = player.getBoundingBox().move(
                    feet.x - player.getX(), feet.y - player.getY(), feet.z - player.getZ());
            if (serverLevel.noCollision(player, moved)) {
                destination = feet;
                break;
            }
        }

        if (destination == null) {
            player.displayClientMessage(Component.translatable("message.forgottenworld.void_staff.blocked"), true);
            return InteractionResult.FAIL;
        }

        Vec3 origin = player.position();
        serverLevel.sendParticles(ParticleTypes.PORTAL, origin.x, origin.y + 1.0, origin.z, 12, 0.3, 0.5, 0.3, 0.1);
        serverLevel.playSound(null, origin.x, origin.y, origin.z,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.8F, 1.2F);

        boolean teleported = serverPlayer.teleportTo(serverLevel, destination.x, destination.y, destination.z,
                Set.<Relative>of(), serverPlayer.getYRot(), serverPlayer.getXRot(), true);
        if (!teleported) {
            player.displayClientMessage(Component.translatable("message.forgottenworld.void_staff.blocked"), true);
            return InteractionResult.FAIL;
        }

        player.resetFallDistance();
        serverLevel.sendParticles(ParticleTypes.PORTAL, destination.x, destination.y + 1.0, destination.z,
                12, 0.3, 0.5, 0.3, 0.1);
        serverLevel.playSound(null, destination.x, destination.y, destination.z,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.8F, 1.0F);

        player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);
        stack.hurtAndBreak(1, player,
                hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);

        return InteractionResult.SUCCESS;
    }
}
