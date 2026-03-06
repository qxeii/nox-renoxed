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

package net.scirave.nox.datagen;


import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.scirave.nox.util.NoxUtil;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

class ItemTagsProvider extends FabricTagProvider.ItemTagProvider {
    public ItemTagsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, @Nullable FabricTagProvider.BlockTagProvider blockTagProvider) {
        super(output, registriesFuture, blockTagProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        this.builder(NoxUtil.FIREPROOF)
                .add(RegistryKey.of(RegistryKeys.ITEM, Identifier.ofVanilla("magma_cream")))
                .add(RegistryKey.of(RegistryKeys.ITEM, Identifier.ofVanilla("wither_skeleton_skull")))
                .add(RegistryKey.of(RegistryKeys.ITEM, Identifier.ofVanilla("nether_star")))
                .add(RegistryKey.of(RegistryKeys.ITEM, Identifier.ofVanilla("ghast_tear")))
                .add(RegistryKey.of(RegistryKeys.ITEM, Identifier.ofVanilla("blaze_rod")))
                .add(RegistryKey.of(RegistryKeys.ITEM, Identifier.ofVanilla("blaze_powder")))
        ;

        this.builder(NoxUtil.TOOLS)
                .addOptionalTag(ItemTags.SWORDS)
                .addOptionalTag(ItemTags.AXES)
                .addOptionalTag(ItemTags.PICKAXES)
                .addOptionalTag(ItemTags.SWORDS)
                .addOptionalTag(ItemTags.HOES)
        ;

        this.builder(NoxUtil.ARMOR)
                .addOptionalTag(ItemTags.HEAD_ARMOR)
                .addOptionalTag(ItemTags.CHEST_ARMOR)
                .addOptionalTag(ItemTags.LEG_ARMOR)
                .addOptionalTag(ItemTags.FOOT_ARMOR);
    }
}
