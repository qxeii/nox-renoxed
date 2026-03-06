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
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.scirave.nox.config.NoxConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BreezeEntity.class)
public abstract class BreezeEntityMixin extends HostileEntityMixin {

    @Override
    public void nox$modifyAttributes(EntityType<?> entityType, World world, CallbackInfo ci) {
        EntityAttributeInstance attr = this.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (attr != null && NoxConfig.breezeBaseHealthMultiplier > 1.0) {
            attr.addTemporaryModifier(new EntityAttributeModifier(Identifier.of("nox:breeze_bonus"), NoxConfig.breezeBaseHealthMultiplier - 1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            this.setHealth(this.getMaxHealth());
        }

        attr = this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE);
        if (attr != null && NoxConfig.breezeFollowRangeMultiplier > 1.0) {
            attr.addTemporaryModifier(new EntityAttributeModifier(Identifier.of("nox:breeze_follow_range_bonus"), NoxConfig.breezeFollowRangeMultiplier - 1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }
}
