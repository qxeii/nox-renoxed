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
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.world.World;
import net.scirave.nox.config.NoxConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(StrayEntity.class)
public abstract class StrayEntityMixin extends AbstractSkeletonEntityMixin {

    protected StrayEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyArg(method = "createArrowProjectile", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ArrowEntity;addEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;)V"))
    public StatusEffectInstance nox$strayBetterSlowness(StatusEffectInstance effect) {
        if (NoxConfig.strayAttacksApplyStrongerSlowness)
            return new StatusEffectInstance(effect.getEffectType(), effect.getDuration(), NoxConfig.straySlownessLevel - 1);
        return effect;
    }

}
