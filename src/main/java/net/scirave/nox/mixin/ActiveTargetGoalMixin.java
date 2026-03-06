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

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.scirave.nox.config.NoxConfig;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(ActiveTargetGoal.class)
public abstract class ActiveTargetGoalMixin extends TrackTargetGoal {

    @Shadow
    protected TargetPredicate targetPredicate;

    @Shadow
    @Nullable
    protected LivingEntity targetEntity;

    public ActiveTargetGoalMixin(MobEntity mob, boolean checkVisibility) {
        super(mob, checkVisibility);
    }

    @Shadow
    protected abstract void findClosestTarget();

    @Inject(method = "canStart", at = @At("HEAD"), cancellable = true)
    public void nox$noRandomTarget(CallbackInfoReturnable<Boolean> cir) {
        if (NoxConfig.mobXray && (this.mob instanceof Monster || this.mob instanceof Angerable || this.mob instanceof GolemEntity)) {
            this.targetPredicate.ignoreVisibility();
        }
        this.findClosestTarget();
        if (this.mob instanceof GuardianEntity && NoxConfig.guardianConstantBeam) {
            return;
        }
        cir.setReturnValue(this.targetEntity != null);
    }
}
