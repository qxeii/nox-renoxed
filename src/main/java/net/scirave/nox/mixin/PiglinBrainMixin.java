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
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.scirave.nox.config.NoxConfig;
import net.scirave.nox.util.NoxUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

@Mixin(value = PiglinBrain.class)
public abstract class PiglinBrainMixin {

    @Inject(method = "isWearingPiglinSafeArmor", at = @At("RETURN"), cancellable = true)
    private static void nox$piglinWearingAllGold(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (NoxConfig.piglinsRequireExclusivelyGoldArmor) {
            if (cir.getReturnValue()) {
                boolean hasGoldenArmor = false;
                for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                    ItemStack stack = entity.getEquippedStack(slot);
                    Item item = stack.getItem();
                    if (NoxUtil.getArmorSlot(item) != null) {
                        if (stack.isIn(ItemTags.PIGLIN_SAFE_ARMOR) || item.getRegistryEntry().isIn(ItemTags.PIGLIN_LOVED)) {
                            hasGoldenArmor = true;
                        } else {
                            cir.setReturnValue(false);
                            return;
                        }
                    }
                }

                cir.setReturnValue(hasGoldenArmor);
            }
        }
    }

}
