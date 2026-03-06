/*
 * -------------------------------------------------------------------
 * Nox
 * Copyright (c) 2026 SciRave
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
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.server.world.ServerWorld;
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

    protected AbstractSkeletonEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At("HEAD"))
    public void nox$skeletonInitGoals(CallbackInfo ci) {
        this.goalSelector.add(0, new Nox$FleeSunlightGoal((AbstractSkeletonEntity) (Object) this, 1.0F));
        this.goalSelector.add(1, new SwimGoal((AbstractSkeletonEntity) (Object) this));
    }

    @Shadow
    protected abstract PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier, ItemStack shotFrom);

    @Inject(method = "shootAt", at = @At("HEAD"), cancellable = true)
    private void nox$adjustArrowShot(LivingEntity target, float pullProgress, CallbackInfo ci) {
        ItemStack bow = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
        ItemStack projectileStack = this.getProjectileType(bow);
        PersistentProjectileEntity projectile = this.createArrowProjectile(projectileStack, pullProgress, bow);
        double x = target.getX() - this.getX();
        double z = target.getZ() - this.getZ();

        if (NoxConfig.skeletonImprovedAim) {
            x += target.getVelocity().x * 10.0D;
            z += target.getVelocity().z * 10.0D;
        }

        double y = target.getBodyY(0.3333333333333333D) - projectile.getY();
        double horizontalDistance = Math.sqrt(x * x + z * z);
        if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
            ProjectileEntity.spawnWithVelocity(projectile, serverWorld, projectileStack, x, y + horizontalDistance * 0.20000000298023224D, z, NoxConfig.skeletonShootArrowPower, 14 - serverWorld.getDifficulty().getId() * 4);
        }

        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        ci.cancel();
    }


    @Override
    public void nox$modifyAttributes(EntityType<?> entityType, World world, CallbackInfo ci) {
        if (NoxConfig.skeletonSpeedMultiplier > 1) {
            this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).addTemporaryModifier(new EntityAttributeModifier(Identifier.of("nox:generic_skeleton_bonus"), NoxConfig.skeletonSpeedMultiplier - 1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }

    @Override
    public boolean nox$canSwim() {
        return NoxConfig.skeletonsCanSwim;
    }


    public boolean nox$isAllowedToMine() {
        return false;
    }
}
