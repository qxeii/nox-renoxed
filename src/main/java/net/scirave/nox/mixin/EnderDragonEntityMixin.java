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

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.world.World;
import net.scirave.nox.config.NoxConfig;
import net.scirave.nox.util.NoxUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin extends MobEntityMixin{

    private static final TargetPredicate nox$RANGE_PREDICATE = TargetPredicate.createAttackable();
    private int nox$fireballCooldown = 0;

    @Override
    public void nox$shouldTakeDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getTypeRegistryEntry().isIn(DamageTypeTags.IS_EXPLOSION)) {
            cir.setReturnValue(!NoxConfig.enderDragonIsImmuneToExplosionDamage);
        }
    }

    @Override
    public void nox$onTick(CallbackInfo ci) {
        if (nox$fireballCooldown <= 0) {
            PlayerEntity player = this.getWorld().getClosestPlayer(nox$RANGE_PREDICATE, (EnderDragonEntity) (Object) this, ((EnderDragonEntity) (Object) this).getX(), ((EnderDragonEntity) (Object) this).getY(), ((EnderDragonEntity) (Object) this).getZ());
            if (player != null && player.squaredDistanceTo((EnderDragonEntity) (Object) this) >= 49.0D && this.canSee(player)) {
                nox$fireballCooldown = NoxConfig.enderDragonFireballCooldown;
                NoxUtil.EnderDragonShootFireball((EnderDragonEntity) (Object) this, player);
            }
        } else {
            nox$fireballCooldown--;
        }

    }

    @Override
    public void nox$modifyAttributes(EntityType<?> entityType, World world, CallbackInfo ci) {
        //Non-applicable
    }

    @Override
    public void nox$hostileAttributes(EntityType<?> entityType, World world, CallbackInfo ci) {
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).addTemporaryModifier(new EntityAttributeModifier("Nox: Enderdragon bonus", NoxConfig.enderDragonBaseHealthMultiplier - 1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        this.setHealth(this.getMaxHealth());
    }

}
