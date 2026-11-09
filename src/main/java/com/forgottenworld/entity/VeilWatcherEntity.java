package com.forgottenworld.entity;

import com.forgottenworld.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class VeilWatcherEntity extends Monster {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.forgottenworld.veil_watcher"),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.PROGRESS);
    private int specialCooldown;

    public VeilWatcherEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 120;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 220.0)
                .add(Attributes.MOVEMENT_SPEED, 0.26)
                .add(Attributes.ATTACK_DAMAGE, 12.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.85);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        if (this.specialCooldown > 0) {
            this.specialCooldown--;
        }
        LivingEntity target = this.getTarget();
        if (target != null && this.specialCooldown <= 0 && this.distanceToSqr(target) < 64.0) {
            this.specialCooldown = 100;
            this.performSpecialAttack(level);
        }
    }

    private void performSpecialAttack(ServerLevel level) {
        this.playSound(SoundEvents.WARDEN_SONIC_BOOM, 1.0F, 0.7F);
        AABB area = this.getBoundingBox().inflate(6.0);
        List<Player> players = level.getEntitiesOfClass(Player.class, area, LivingEntity::isAlive);
        for (Player p : players) {
            p.hurtServer(level, this.damageSources().mobAttack(this), 6.0F);
            p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0));
            p.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1));
            p.push((p.getX() - this.getX()) * 0.2, 0.15, (p.getZ() - this.getZ()) * 0.2);
        }
        level.sendParticles(ParticleTypes.PORTAL, this.getX(), this.getY() + 1.5, this.getZ(), 60, 2.0, 1.5, 2.0, 0.2);
        level.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY() + 1.0, this.getZ(), 20, 1.5, 0.5, 1.5, 0.05);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        this.spawnAtLocation(level, new ItemStack(ModItems.DIMENSION_KEY));
        this.spawnAtLocation(level, new ItemStack(ModItems.UMBRAL_INGOT, 2 + this.random.nextInt(3)));
        this.spawnAtLocation(level, new ItemStack(ModItems.ETHER_SHARD, 3 + this.random.nextInt(4)));
        this.spawnAtLocation(level, new ItemStack(ModItems.GLOOM_CRYSTAL, 1 + this.random.nextInt(2)));
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }
}
