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

package net.scirave.nox.datagen;


import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.scirave.nox.util.NoxUtil;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

class ItemTagsProvider extends FabricTagProvider.ItemTagProvider {
    public ItemTagsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, @Nullable FabricTagProvider.BlockTagProvider blockTagProvider) {
        super(output, registriesFuture, blockTagProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        this.getOrCreateTagBuilder(NoxUtil.FIREPROOF)
                .add(Items.MAGMA_CREAM)
                .add(Items.WITHER_SKELETON_SKULL)
                .add(Items.NETHER_STAR)
                .add(Items.GHAST_TEAR)
                .add(Items.BLAZE_ROD)
                .add(Items.BLAZE_POWDER)
        ;

        this.getOrCreateTagBuilder(NoxUtil.TOOLS)
                .addOptionalTag(ItemTags.SWORDS)
                .addOptionalTag(ItemTags.AXES)
                .addOptionalTag(ItemTags.PICKAXES)
                .addOptionalTag(ItemTags.SWORDS)
                .addOptionalTag(ItemTags.HOES)
        ;

        var armor = this.getOrCreateTagBuilder(NoxUtil.ARMOR);
        Registries.ITEM.stream().filter((item) -> item instanceof ArmorItem).forEach(armor::add);
    }
}
