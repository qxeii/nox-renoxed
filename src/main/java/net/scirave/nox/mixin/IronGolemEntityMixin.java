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

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.scirave.nox.config.NoxConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(IronGolemEntity.class)
public abstract class IronGolemEntityMixin extends GolemEntityMixin {

    @Shadow
    public abstract boolean canTarget(EntityType<?> type);

    @Shadow
    public abstract boolean tryAttack(ServerWorld world, Entity target);

    private boolean nox$canSweepAttack = true;

    @Inject(method = "tryAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;onTargetDamaged(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;)V"))
    public void nox$ironGolemSweepAttack(ServerWorld world, Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (NoxConfig.ironGolemsHaveASweepAttack) {
            if (this.nox$canSweepAttack) {
                this.nox$canSweepAttack = false;
                List<MobEntity> list = this.getEntityWorld().getEntitiesByClass(MobEntity.class, Box.of(target.getEntityPos(), 1, 1, 1), (mob) -> (mob instanceof Monster || mob.getTarget() == (Object) this) && this.canTarget(mob.getType()) && this.canTarget(mob));
                for (MobEntity mob : list) {
                    this.tryAttack(world, mob);
                }
            }
            this.nox$canSweepAttack = true;
        }
    }

}
