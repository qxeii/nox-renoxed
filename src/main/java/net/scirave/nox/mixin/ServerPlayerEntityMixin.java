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

import com.mojang.datafixers.util.Either;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.scirave.nox.config.NoxConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = ServerPlayerEntity.class, priority = 100)
public abstract class ServerPlayerEntityMixin extends LivingEntityMixin{


    @Inject(method = "trySleep", at = @At("RETURN"), cancellable = true)
    public void nox$sleepNerf(BlockPos pos, CallbackInfoReturnable<Either<PlayerEntity.SleepFailureReason, Unit>> cir) {
        if (cir.getReturnValue().left().isPresent()) {
            return;
        }

        Vec3d vec3d = Vec3d.ofBottomCenter(pos);
        int seaLevel = ((ServerPlayerEntity) (Object) this).getEntityWorld().getSeaLevel();
        int horizontalSearchDistance = NoxConfig.sleepHorizontalSearchDistance;
        int minVerticalSearchDistance = NoxConfig.sleepMinVerticalSearchDistance;
        boolean extendToSeaLevel = NoxConfig.sleepExtendToSeaLevel;

        double upperY = extendToSeaLevel ? Math.max(vec3d.getY() + minVerticalSearchDistance, seaLevel) : vec3d.getY() + minVerticalSearchDistance;
        double lowerY = extendToSeaLevel ? Math.min(vec3d.getY() - minVerticalSearchDistance, seaLevel) : vec3d.getY() - minVerticalSearchDistance;

        ServerWorld world = (ServerWorld) ((ServerPlayerEntity) (Object) this).getEntityWorld();
        List<HostileEntity> list = world.getEntitiesByClass(HostileEntity.class, new Box(
                vec3d.getX() - horizontalSearchDistance, lowerY, vec3d.getZ() - horizontalSearchDistance,
                vec3d.getX() + horizontalSearchDistance, upperY, vec3d.getZ() + horizontalSearchDistance),
            hostileEntity -> hostileEntity.isAngryAt(world, (ServerPlayerEntity) (Object) this));
        if (!list.isEmpty()) {
            if (NoxConfig.sleepApplyGlowing) {
                list.forEach((hostile) -> hostile.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 60, 0, false, false)));
            }

            cir.setReturnValue(Either.left(PlayerEntity.SleepFailureReason.NOT_SAFE));
        }
    }
}
