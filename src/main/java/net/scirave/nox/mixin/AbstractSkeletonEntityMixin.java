/*
 * -------------------------------------------------------------------
 * Nox
 * Copyright (c) 2024 SciRave
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * -------------------------------------------------------------------
 */

package net.scirave.nox.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.scirave.nox.config.NoxConfig;
import net.scirave.nox.goals.Nox$FleeSunlightGoal;
import net.scirave.nox.util.Nox$SwimGoalInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeletonEntity.class)
public abstract class AbstractSkeletonEntityMixin extends HostileEntity implements Nox$SwimGoalInterface, RangedAttackMob{

    private Vec3d nox$targetVelocity = Vec3d.ZERO;
    private Vec3d nox$lastTargetVelocity = Vec3d.ZERO;
    private Vec3d nox$velocityDifference = Vec3d.ZERO;

    protected AbstractSkeletonEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow protected abstract PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier);

    @Inject(method = "initGoals", at = @At("HEAD"))
    public void nox$skeletonInitGoals(CallbackInfo ci) {
        this.goalSelector.add(0, new Nox$FleeSunlightGoal((AbstractSkeletonEntity) (Object) this, 1.0F));
        this.goalSelector.add(1, new SwimGoal((AbstractSkeletonEntity) (Object) this));
    }

    @Inject(method = "shootAt", at = @At("HEAD"), cancellable = true )
    public void nox$skeletonAttack(LivingEntity target, float pullProgress, CallbackInfo ci) {
        ItemStack itemStack = this.getProjectileType(this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW)));
        PersistentProjectileEntity persistentProjectileEntity = this.createArrowProjectile(itemStack, pullProgress);
        Position targetPos = target.getPos().add(this.nox$targetVelocity.multiply(10));
        Position shooterPos = this.getPos();
        double d = targetPos.getX() - shooterPos.getX();
        double e = this.getTarget().getY() - persistentProjectileEntity.getY();
        double f = targetPos.getZ() - shooterPos.getZ();
        double g = Math.sqrt(d * d + f * f);
        persistentProjectileEntity.setVelocity(d, e + g * 0.20000000298023224D, f, 2F, (float)(14 - this.getWorld().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.getWorld().spawnEntity(persistentProjectileEntity);
        ci.cancel();
        return;
    }

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    public void nox$onTick(CallbackInfo ci) {
        if(this.getTarget() != null) {
            this.nox$velocityDifference = this.nox$velocityDifference.multiply(3).add(this.getTarget().getVelocity().subtract(this.nox$lastTargetVelocity)).multiply(0.25);
            this.nox$targetVelocity = this.nox$targetVelocity.multiply(3).add(this.getTarget().getVelocity().add(this.nox$velocityDifference.multiply(5))).multiply(0.25);
            this.nox$lastTargetVelocity = this.getTarget().getVelocity();
        }
    }

    @Override
    public void nox$modifyAttributes(EntityType<?> entityType, World world, CallbackInfo ci) {
        if (NoxConfig.skeletonSpeedMultiplier > 1) {
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addTemporaryModifier(new EntityAttributeModifier("Nox: Skeleton bonus", NoxConfig.skeletonSpeedMultiplier - 1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }

    @Override
    public boolean nox$canSwim() {
        return NoxConfig.skeletonsCanSwim;
    }


    public abstract boolean nox$isAllowedToMine();
}
