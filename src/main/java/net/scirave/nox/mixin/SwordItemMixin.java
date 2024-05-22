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

import net.minecraft.component.type.ToolComponent;
import net.minecraft.item.SwordItem;
import net.scirave.nox.Nox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.List;

@Mixin(SwordItem.class)
public class SwordItemMixin {

    @ModifyArg(method = "createToolComponent", at = @At(value = "INVOKE", target = "Lnet/minecraft/component/type/ToolComponent;<init>(Ljava/util/List;FI)V"))
    private static List<ToolComponent.Rule> nox$createToolComponent(List<ToolComponent.Rule> rules) {
        var list = new ArrayList<>(rules);
        list.add(ToolComponent.Rule.ofAlwaysDropping(List.of(Nox.NOX_COBWEB), 15));
        return list;
    }
}
