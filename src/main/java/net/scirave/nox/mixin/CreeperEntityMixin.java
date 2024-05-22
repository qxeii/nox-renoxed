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

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.world.World;
import net.scirave.nox.config.NoxConfig;
import net.scirave.nox.goals.Nox$CreeperBreachGoal;
import net.scirave.nox.util.Nox$CreeperBreachInterface;
import net.scirave.nox.util.Nox$PouncingEntityInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.scirave.nox.config.NoxConfig.creepersExplodeOnDeath;


@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends HostileEntityMixin implements Nox$CreeperBreachInterface, Nox$PouncingEntityInterface{

    @Shadow protected abstract void explode();

    @Inject(method = "initGoals", at = @At("TAIL"))
    public void nox$creeperInitGoals(CallbackInfo ci) {
        this.goalSelector.add(2, new PounceAtTargetGoal((CreeperEntity) (Object) this, 0.3F));
        this.goalSelector.add(2, new FleeEntityGoal((CreeperEntity) (Object) this, LivingEntity.class,
                4.0F, 1.5D, 1.7D, (living) -> {
            if (!NoxConfig.creepersRunFromShields) return false;
            if (living instanceof LivingEntity livingEntity) {
                return livingEntity.isBlocking() && livingEntity.blockedByShield(this.getWorld().getDamageSources().explosion((CreeperEntity) (Object) this, (CreeperEntity) (Object) this));
            }
            return false;
        }));
        if (NoxConfig.creeperBreachDistance > 0) {
            this.goalSelector.add(3, new Nox$CreeperBreachGoal((CreeperEntity) (Object) this));
        }
    }

    @Override
    public void nox$modifyAttributes(EntityType<?> entityType, World world, CallbackInfo ci) {
        if (NoxConfig.creeperSpeedMultiplier > 1) {
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addTemporaryModifier(new EntityAttributeModifier("Nox: Creeper speed bonus", NoxConfig.creeperSpeedMultiplier - 1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }

    @Override
    public void nox$onRemoveEntity(Entity.RemovalReason reason, CallbackInfo ci) {
        if (creepersExplodeOnDeath) {
            if (reason == Entity.RemovalReason.KILLED) {
                ci.cancel();
                ((CreeperEntity) (Object) this).remove(Entity.RemovalReason.DISCARDED);
                explode();
            }
        }
    }

    @Override
    public boolean nox$isAllowedToBreachWalls() {
        return NoxConfig.creepersBreachWalls;
    }

    @Override
    public boolean nox$isAllowedToPounce() {
        return NoxConfig.creepersPounceAtTarget;
    }

    @Override
    public int nox$pounceCooldown() {
        return NoxConfig.creepersPounceCooldown;
    }
}