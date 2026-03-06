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
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.scirave.nox.config.NoxConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowGolemEntity.class)
public abstract class SnowGolemEntityMixin extends GolemEntityMixin {

    @ModifyArgs(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/ProjectileAttackGoal;<init>(Lnet/minecraft/entity/ai/RangedAttackMob;DIF)V"))
    public void nox$snowGolemFasterShooting(Args args) {
        args.set(2, (int)(((int) args.get(2)) / Math.max(NoxConfig.snowGolemAttackRechargeSpeedMultiplier, 0)));
        args.set(3, ((float) args.get(3)) * Math.max(NoxConfig.snowGolemAttackRangeMultiplier, 1));
    }

    @Inject(method = "shootAt", at = @At("HEAD"), cancellable = true)
    public void nox$snowGolemShotMixin(LivingEntity target, float pullProgress, CallbackInfo ci) {
        if (!(this.getEntityWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        ItemStack stack = new ItemStack(Items.SNOWBALL);
        double x = target.getX() - this.getX();
        double y = target.getEyeY() - 1.100000023841858D;
        double z = target.getZ() - this.getZ();
        double arc = Math.sqrt(x * x + z * z) * 0.20000000298023224D;

        ProjectileEntity.spawn(new SnowballEntity(serverWorld, (SnowGolemEntity) (Object) this, stack), serverWorld, stack, projectile -> projectile.setVelocity(x, y + arc, z, NoxConfig.snowGolemShotSpeed, NoxConfig.snowGolemInverseAccuracy));
        ci.cancel();
    }
}
