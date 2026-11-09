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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class VeilWatcherEntity extends Monster {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.forgottenworld.veil_watcher"),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.PROGRESS);
    private int attackCooldown;
    private int attackMode;
    private boolean enragedPhase;

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
        float progress = this.getMaxHealth() <= 0 ? 0 : this.getHealth() / this.getMaxHealth();
        this.bossEvent.setProgress(progress);

        if (!enragedPhase && progress <= 0.35F) {
            enragedPhase = true;
            this.bossEvent.setColor(BossEvent.BossBarColor.RED);
            this.bossEvent.setName(Component.translatable("entity.forgottenworld.veil_watcher.enraged"));
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.33);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(16.0);
            this.playSound(SoundEvents.ENDER_DRAGON_GROWL, 1.2F, 0.7F);
            level.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY() + 1.5, this.getZ(), 80, 1.8, 1.5, 1.8, 0.05);
        }

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
        LivingEntity target = this.getTarget();
        if (target != null && this.attackCooldown <= 0) {
            double distSq = this.distanceToSqr(target);
            int baseCd = enragedPhase ? 55 : 90;
            if (distSq < 64.0) {
                this.attackCooldown = baseCd;
                if (attackMode % 3 == 0) shadowPulse(level);
                else if (attackMode % 3 == 1) voidPull(level, target);
                else veilSlam(level);
                attackMode++;
            } else if (enragedPhase && distSq < 144.0) {
                this.attackCooldown = 70;
                voidPull(level, target);
            }
        }
        if (enragedPhase && this.tickCount % 20 == 0) {
            level.sendParticles(ParticleTypes.PORTAL, this.getX(), this.getY() + 1.2, this.getZ(), 8, 0.8, 1.0, 0.8, 0.02);
        }
    }

    private void shadowPulse(ServerLevel level) {
        this.playSound(SoundEvents.WARDEN_SONIC_BOOM, 1.0F, 0.7F);
        AABB area = this.getBoundingBox().inflate(6.0);
        for (Player p : level.getEntitiesOfClass(Player.class, area, LivingEntity::isAlive)) {
            p.hurtServer(level, this.damageSources().mobAttack(this), enragedPhase ? 8.0F : 6.0F);
            p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0));
            p.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, enragedPhase ? 1 : 0));
        }
        level.sendParticles(ParticleTypes.PORTAL, this.getX(), this.getY() + 1.5, this.getZ(), 60, 2.0, 1.5, 2.0, 0.2);
    }

    private void voidPull(ServerLevel level, LivingEntity target) {
        this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 0.6F);
        Vec3 pull = this.position().subtract(target.position()).normalize().scale(enragedPhase ? 0.9 : 0.6);
        target.push(pull.x, 0.15, pull.z);
        target.hurtServer(level, this.damageSources().magic(), enragedPhase ? 5.0F : 3.0F);
        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0));
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, target.getX(), target.getY() + 1.0, target.getZ(), 25, 0.5, 0.8, 0.5, 0.05);
    }

    private void veilSlam(ServerLevel level) {
        this.playSound(SoundEvents.GENERIC_EXPLODE.value(), 0.8F, 0.6F);
        AABB area = this.getBoundingBox().inflate(4.5);
        for (Player p : level.getEntitiesOfClass(Player.class, area, LivingEntity::isAlive)) {
            p.hurtServer(level, this.damageSources().mobAttack(this), enragedPhase ? 10.0F : 7.0F);
            Vec3 away = p.position().subtract(this.position()).normalize().scale(enragedPhase ? 1.2 : 0.8);
            p.push(away.x, 0.35, away.z);
        }
        level.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 40, 1.8, 0.2, 1.8, 0.02);
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY() + 0.8, this.getZ(), 20, 1.2, 0.3, 1.2, 0.02);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        this.spawnAtLocation(level, new ItemStack(ModItems.DIMENSION_KEY));
        this.spawnAtLocation(level, new ItemStack(ModItems.UMBRAL_INGOT, 2 + this.random.nextInt(3)));
        this.spawnAtLocation(level, new ItemStack(ModItems.ETHER_SHARD, 3 + this.random.nextInt(4)));
        this.spawnAtLocation(level, new ItemStack(ModItems.GLOOM_CRYSTAL, 1 + this.random.nextInt(2)));
        this.spawnAtLocation(level, new ItemStack(ModItems.VOID_STAFF));
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }
}
