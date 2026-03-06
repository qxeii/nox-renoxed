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

package net.scirave.nox;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.scirave.nox.config.NoxConfig;
import net.scirave.nox.polymer.blocks.NoxCobwebBlock;
import net.scirave.nox.polymer.blocks.NoxCobwebBlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Nox implements ModInitializer {

    public static String MOD_ID = "nox";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Identifier NOX_COBWEB_ID = Identifier.of(MOD_ID, "cobweb");
    public static final RegistryKey<Block> NOX_COBWEB_KEY = RegistryKey.of(RegistryKeys.BLOCK, NOX_COBWEB_ID);
    public static final Block NOX_COBWEB = new NoxCobwebBlock(AbstractBlock.Settings.copy(Blocks.COBWEB).registryKey(NOX_COBWEB_KEY));
    public static BlockEntityType<NoxCobwebBlockEntity> NOX_COBWEB_BLOCK_ENTITY;

    @Override
    public void onInitialize() {
        NoxConfig.init(MOD_ID, NoxConfig.class);
        NoxConfig.write(MOD_ID);
        Registry.register(Registries.BLOCK, NOX_COBWEB_ID, NOX_COBWEB);
        PolymerBlockUtils.registerBlockEntity(NOX_COBWEB_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(MOD_ID, "cobweb_block_entity"),
                FabricBlockEntityTypeBuilder.create(NoxCobwebBlockEntity::new, NOX_COBWEB).build()));
    }
}
