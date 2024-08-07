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

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.scirave.nox.config.NoxConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = SlimeEntity.class)
public abstract class SlimeEntityMixin extends MobEntityMixin {

    @Shadow
    public abstract int getSize();

    @Shadow
    public abstract void setSize(int size, boolean heal);

    @Shadow
    public abstract EntityType<? extends SlimeEntity> getType();

    @Inject(method = "canSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/ChunkRandom;getSlimeRandom(IIJJ)Lnet/minecraft/util/math/random/Random;"), cancellable = true)
    private static void nox$slimeSpawnNaturally(EntityType<SlimeEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir) {
        if (NoxConfig.slimeNaturalSpawn) {
            if (world.getLightLevel(LightType.BLOCK, pos) <= NoxConfig.slimeNaturalSpawnMaxBlockLight
                    && world.getLightLevel(LightType.SKY, pos) - world.getAmbientDarkness() <= NoxConfig.slimeNaturalSpawnMaxSkyLight) {
                cir.setReturnValue(SlimeEntity.canMobSpawn(type, world, spawnReason, pos, random));
            }
        }
    }

    @Inject(method = "initialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;initialize(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/LocalDifficulty;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/entity/EntityData;)Lnet/minecraft/entity/EntityData;"))
    public void nox$betterSlimeSpawn(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, CallbackInfoReturnable<EntityData> cir) {
        int size = 3;
        float random = this.getRandom().nextFloat() * 10 + difficulty.getClampedLocalDifficulty();

        if (random < 5) {
            size = 2;
        }
        if (random < 2) {
            size = 1;
        }
        if (random > 9.5) {
            size = 4;
        }

        this.setSize(size, true);
    }

    @Inject(method = "getTicksUntilNextJump", at = @At("HEAD"), cancellable = true)
    private void nox$makeSlimesJumpConstantly(CallbackInfoReturnable<Integer> cir) {
        if (NoxConfig.slimesJumpConstantly)
            cir.setReturnValue(4);
    }

    @Inject(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/SlimeEntity;setSize(IZ)V"))
    public void nox$slimeReapplyAttributes(Entity.RemovalReason reason, CallbackInfo ci, @Local(ordinal = 1) SlimeEntity slimeEntity) {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            slimeEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(this.getBlockPos()), SpawnReason.REINFORCEMENT, null);
        }
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;onTargetDamaged(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;)V"))
    public void nox$slimeOnAttack(LivingEntity victim, CallbackInfo ci) {
        //Overridden
    }

    @Override
    public void nox$modifyAttributes(EntityType<?> entityType, World world, CallbackInfo ci) {
        EntityAttributeInstance attr;
        if (NoxConfig.slimeBaseHealthMultiplier > 1) {
            attr = this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            if (attr != null) {
                attr.addTemporaryModifier(new EntityAttributeModifier(Identifier.of("nox:slime_bonus"), NoxConfig.slimeBaseHealthMultiplier - 1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                this.setHealth(this.getMaxHealth());
            }
        }
        if (NoxConfig.slimeFollowRangeMultiplier > 1) {
            attr = this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE);
            if (attr != null)
                attr.addTemporaryModifier(new EntityAttributeModifier(Identifier.of("nox:slime_bonus"), NoxConfig.slimeFollowRangeMultiplier - 1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
        if (NoxConfig.slimeMoveSpeedMultiplier > 1) {
            attr = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (attr != null)
                attr.addTemporaryModifier(new EntityAttributeModifier(Identifier.of("nox:slime_bonus"), NoxConfig.slimeMoveSpeedMultiplier - 1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }

        attr = this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
        if (attr != null)
            attr.addTemporaryModifier(new EntityAttributeModifier(Identifier.of("nox:slime_bonus"), 0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    @Override
    public void nox$shouldTakeDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        super.nox$shouldTakeDamage(source, amount, cir);
        if (source.getName().equals("fall"))
            cir.setReturnValue(!NoxConfig.slimesImmuneToFallDamage);
        else if (source.getTypeRegistryEntry().isIn(DamageTypeTags.IS_PROJECTILE) && !source.getTypeRegistryEntry().isIn(DamageTypeTags.BYPASSES_ARMOR))
            cir.setReturnValue(!NoxConfig.slimesResistProjectiles);
    }

    @Override
    public void nox$onDeath(DamageSource source, CallbackInfo ci) {
        if (this.getWorld() instanceof ServerWorld) {
            this.nox$slimeOnDeath();
        }
    }

    public void nox$slimeOnDeath() {
        if (NoxConfig.slimePoisonCloudOnDeath && NoxConfig.slimePoisonCloudDurationDivisor > 0 && this.getType() == EntityType.SLIME) {
            AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(this.getWorld(), this.getX(), this.getY(), this.getZ());
            cloud.setRadius(NoxConfig.slimePoisonCloudRadiusMultiplier * this.getSize());
            cloud.setRadiusOnUse(-0.5F);
            cloud.setWaitTime(10 + 15 * (this.getSize() - 1));
            cloud.setDuration(cloud.getDuration() * this.getSize() / NoxConfig.slimePoisonCloudDurationDivisor);
            cloud.setRadiusGrowth(-cloud.getRadius() / (float) cloud.getDuration());
            cloud.addEffect(new StatusEffectInstance(StatusEffects.POISON, NoxConfig.slimePoisonDuration, NoxConfig.slimePoisonAmplifier - 1));
            this.getWorld().spawnEntity(cloud);
        }
    }

    @Override
    public void nox$onStatusEffect(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        if (effect.getEffectType() == StatusEffects.POISON) {
            cir.setReturnValue(false);
        }
    }

}
