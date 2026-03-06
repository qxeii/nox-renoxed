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

import net.minecraft.entity.projectile.thrown.LingeringPotionEntity;
import net.scirave.nox.config.NoxConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LingeringPotionEntity.class)
public abstract class PotionEntityMixin extends ProjectileEntityMixin {

    @ModifyArg(method = "spawnAreaEffectCloud", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/AreaEffectCloudEntity;setRadius(F)V"))
    public float nox$witchBiggerPotionRadius(float original) {
        if(NoxConfig.witchesUseLingeringPotions){
            return original * NoxConfig.witchLingeringPotionRadiusMultiplier;
        }
        return original;
    }

    @ModifyArg(method = "spawnAreaEffectCloud", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/AreaEffectCloudEntity;setWaitTime(I)V"))
    public int nox$witchFasterCloudWindup(int original) {
        return original / NoxConfig.witchPotionWindupDivisor;
    }

}
