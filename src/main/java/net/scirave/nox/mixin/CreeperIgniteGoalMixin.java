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
import net.minecraft.entity.ai.goal.CreeperIgniteGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Position;
import net.scirave.nox.config.NoxConfig;
import net.scirave.nox.util.NoxUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumSet;

@Mixin(CreeperIgniteGoal.class)
public abstract class CreeperIgniteGoalMixin extends Goal {

    @Shadow
    @Nullable
    private LivingEntity target;

    @Shadow
    @Final
    private CreeperEntity creeper;

    @Shadow
    public abstract void stop();

    @Inject(method = "<init>", at = @At("TAIL"))
    public void nox$creeperIgniteWhileMoving(CreeperEntity creeper, CallbackInfo ci) {
        EnumSet<Control> empty = EnumSet.noneOf(Control.class);
        this.setControls(empty);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void nox$creeperSmarterIgnite(CallbackInfo ci) {
        double d = this.creeper.squaredDistanceTo(this.target);
        if (this.target == null) {
            this.creeper.setFuseSpeed(-1);
        } else if (d > 16.0D) {
            this.creeper.setFuseSpeed(-1);
        } else if (!NoxConfig.creepersAttackShields && NoxUtil.isBlockingDamage(this.target, this.creeper.getEntityWorld().getDamageSources().explosion(this.creeper, this.creeper))) {
            this.creeper.setFuseSpeed(-1);
        } else {
            this.creeper.setFuseSpeed(1);
        }
    }

    @Inject(method = "canStart", at = @At("RETURN"), cancellable = true)
    public void nox$creeperNoTargetShield(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity victim = this.creeper.getTarget();
        if (!NoxConfig.creepersAttackShields && cir.getReturnValue() && victim != null && NoxUtil.isBlockingDamage(victim, this.creeper.getEntityWorld().getDamageSources().explosion(this.creeper, this.creeper))) {
            this.creeper.setFuseSpeed(-1);
            cir.setReturnValue(false);
        }
    }

}
