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
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.scirave.nox.util.Nox$MiningInterface;
import net.scirave.nox.util.Nox$PouncingEntityInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
@Mixin(PounceAtTargetGoal.class)
public abstract class PounceAtTargetMixin extends Goal {

    protected long nox$lastPounceUsage = 0;
    @Shadow
    private LivingEntity target;
    @Shadow
    @Final
    private MobEntity mob;

    @ModifyArgs(method = "start", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;setVelocity(DDD)V"))
    public void nox$betterPounce(Args args) {
        args.set(0, ((double) args.get(0)) * 2);
        args.set(2, ((double) args.get(2)) * 2);
    }

    @Inject(method = "canStart", at = @At("HEAD"), cancellable = true)
    public void nox$betterPounce(CallbackInfoReturnable<Boolean> cir) {
        if (mob instanceof Nox$PouncingEntityInterface && ((Nox$PouncingEntityInterface) mob).nox$isAllowedToPounce()) {
            this.target = this.mob.getTarget();
            if (this.target != null && this.mob.isOnGround() && !((Nox$MiningInterface) this.mob).nox$isMining()) {
                double d = this.mob.squaredDistanceTo(this.target);
                if (!(d <= 4.0D) && !(d >= 16.0D)) {
                    if (this.mob.getY() >= (this.target.getY()) - 0.1) {
                        if ((this.mob.getEntityWorld().getTime() - this.nox$lastPounceUsage) >= ((Nox$PouncingEntityInterface) mob).nox$pounceCooldown()) {
                            this.nox$lastPounceUsage = this.mob.getEntityWorld().getTime();
                            this.mob.getLookControl().lookAt(this.target);
                            Hand hand = this.mob.preferredHand;
                            if (hand != null) {
                                if (d < 1.0D && this.mob.getEntityWorld() instanceof ServerWorld serverWorld) {
                                    this.mob.tryAttack(serverWorld, target);
                                }
                                this.mob.swingHand(hand);
                            }
                            cir.setReturnValue(true);
                            return;
                        }
                    }
                }
            }
        }
        cir.setReturnValue(false);
    }
}
