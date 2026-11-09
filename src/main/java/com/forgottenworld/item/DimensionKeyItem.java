package com.forgottenworld.item;

import com.forgottenworld.dimension.ModDimensions;
import com.forgottenworld.entity.ModEntities;
import com.forgottenworld.entity.VeilWatcherEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class DimensionKeyItem extends Item {
    public DimensionKeyItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        // In the Forgotten dimension, the key can summon the Veil Watcher (boss).
        if (serverLevel.dimension().equals(ModDimensions.FORGOTTEN_LEVEL) && player.isShiftKeyDown()) {
            AABB area = player.getBoundingBox().inflate(64.0);
            boolean bossNearby = !serverLevel.getEntitiesOfClass(VeilWatcherEntity.class, area, e -> true).isEmpty();
            if (bossNearby) {
                player.displayClientMessage(Component.translatable("message.forgottenworld.dimension_key.boss_nearby"), true);
                return InteractionResult.FAIL;
            }
            VeilWatcherEntity boss = ModEntities.VEIL_WATCHER.create(serverLevel, EntitySpawnReason.EVENT);
            if (boss != null) {
                boss.setPos(player.getX(), player.getY() + 1.0, player.getZ());
                boss.setYRot(player.getYRot());
                serverLevel.addFreshEntity(boss);
                serverLevel.playSound(null, player.blockPosition(), SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0F, 0.8F);
                player.displayClientMessage(Component.translatable("message.forgottenworld.dimension_key.summoned"), true);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                player.getCooldowns().addCooldown(stack, 200);
                return InteractionResult.SUCCESS;
            }
        }

        player.displayClientMessage(Component.translatable("message.forgottenworld.dimension_key.use"), true);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.8F, 0.9F);
        player.getCooldowns().addCooldown(stack, 40);
        return InteractionResult.SUCCESS;
    }
}
