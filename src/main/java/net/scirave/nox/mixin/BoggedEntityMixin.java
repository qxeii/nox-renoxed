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
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.BoggedEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;
import net.scirave.nox.config.NoxConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BoggedEntity.class)
public abstract class BoggedEntityMixin extends AbstractSkeletonEntityMixin {

    protected BoggedEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyArg(method = "createArrowProjectile", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ArrowEntity;addEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;)V"))
    private StatusEffectInstance nox$boggedStrongerPoison(StatusEffectInstance effect) {
        return new StatusEffectInstance(effect.getEffectType(), NoxConfig.boggedPoisonDuration, Math.max(0, NoxConfig.boggedPoisonLevel - 1));
    }
}
