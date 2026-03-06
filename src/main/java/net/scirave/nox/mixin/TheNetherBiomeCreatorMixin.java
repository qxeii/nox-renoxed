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
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.TheNetherBiomeCreator;
import net.scirave.nox.config.NoxConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TheNetherBiomeCreator.class)
public class TheNetherBiomeCreatorMixin {

    @Redirect(method = "createNetherWastes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/SpawnSettings$Builder;spawn(Lnet/minecraft/entity/SpawnGroup;ILnet/minecraft/world/biome/SpawnSettings$SpawnEntry;)Lnet/minecraft/world/biome/SpawnSettings$Builder;", ordinal = 0))
    private static SpawnSettings.Builder nox$adjustNetherWastesSpawns(SpawnSettings.Builder instance, SpawnGroup spawnGroup, int weight, SpawnSettings.SpawnEntry spawnEntry) {
        if (NoxConfig.blazeNaturalSpawn) {
            instance.spawn(SpawnGroup.MONSTER, 10, new SpawnSettings.SpawnEntry(EntityType.BLAZE, 2, 3));
        }
        return instance.spawn(spawnGroup, MathHelper.ceil(weight * 1.5f), new SpawnSettings.SpawnEntry(EntityType.GHAST, spawnEntry.minGroupSize(), spawnEntry.maxGroupSize()));
    }

    @Redirect(method = "createBasaltDeltas", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/SpawnSettings$Builder;spawn(Lnet/minecraft/entity/SpawnGroup;ILnet/minecraft/world/biome/SpawnSettings$SpawnEntry;)Lnet/minecraft/world/biome/SpawnSettings$Builder;", ordinal = 0))
    private static SpawnSettings.Builder nox$adjustBasaltDeltasSpawns(SpawnSettings.Builder instance, SpawnGroup spawnGroup, int weight, SpawnSettings.SpawnEntry spawnEntry) {
        if (NoxConfig.blazeNaturalSpawn) {
            instance.spawn(SpawnGroup.MONSTER, 20, new SpawnSettings.SpawnEntry(EntityType.BLAZE, 1, 3));
        }
        return instance.spawn(spawnGroup, MathHelper.ceil(weight * 1.5f), new SpawnSettings.SpawnEntry(EntityType.GHAST, spawnEntry.minGroupSize(), spawnEntry.maxGroupSize() * 4));
    }

    @Redirect(method = "createCrimsonForest", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/SpawnSettings$Builder;spawn(Lnet/minecraft/entity/SpawnGroup;ILnet/minecraft/world/biome/SpawnSettings$SpawnEntry;)Lnet/minecraft/world/biome/SpawnSettings$Builder;", ordinal = 0))
    private static SpawnSettings.Builder nox$adjustCrimsonForestSpawns(SpawnSettings.Builder instance, SpawnGroup spawnGroup, int weight, SpawnSettings.SpawnEntry spawnEntry) {
        if (NoxConfig.blazeNaturalSpawn) {
            instance.spawn(SpawnGroup.MONSTER, 3, new SpawnSettings.SpawnEntry(EntityType.BLAZE, 1, 3));
        }
        instance.spawn(spawnGroup, 60, new SpawnSettings.SpawnEntry(EntityType.GHAST, 1, 4));
        if (NoxConfig.spawnGhastsInMoreBiomes)
            instance.spawn(spawnGroup, NoxConfig.increaseGhastSpawns ? 60 : 40, new SpawnSettings.SpawnEntry(EntityType.GHAST, 1, 4));
        return instance.spawn(spawnGroup, weight, spawnEntry);
    }

    @Redirect(method = "createWarpedForest", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/SpawnSettings$Builder;spawn(Lnet/minecraft/entity/SpawnGroup;ILnet/minecraft/world/biome/SpawnSettings$SpawnEntry;)Lnet/minecraft/world/biome/SpawnSettings$Builder;", ordinal = 0))
    private static SpawnSettings.Builder nox$adjustWarpedForestSpawns(SpawnSettings.Builder instance, SpawnGroup spawnGroup, int weight, SpawnSettings.SpawnEntry spawnEntry) {
        if (NoxConfig.witherSkeletonsSpawnNaturallyInNether)
            instance.spawn(SpawnGroup.MONSTER, 40, new SpawnSettings.SpawnEntry(EntityType.WITHER_SKELETON, 1, 4));
        if (NoxConfig.spawnGhastsInMoreBiomes)
            instance.spawn(spawnGroup, NoxConfig.increaseGhastSpawns ? 30 : 20, new SpawnSettings.SpawnEntry(EntityType.GHAST, 1, 4));
        return instance.spawn(spawnGroup, 100, new SpawnSettings.SpawnEntry(EntityType.ENDERMAN, 4, 4));
    }

    @Redirect(method = "createSoulSandValley", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/SpawnSettings$Builder;spawn(Lnet/minecraft/entity/SpawnGroup;ILnet/minecraft/world/biome/SpawnSettings$SpawnEntry;)Lnet/minecraft/world/biome/SpawnSettings$Builder;", ordinal = 1))
    private static SpawnSettings.Builder nox$adjustSoulSandValleySpawns(SpawnSettings.Builder instance, SpawnGroup spawnGroup, int weight, SpawnSettings.SpawnEntry spawnEntry) {
        if (NoxConfig.witherSkeletonsSpawnNaturallyInNether)
            instance.spawn(SpawnGroup.MONSTER, 5, new SpawnSettings.SpawnEntry(EntityType.WITHER_SKELETON, 1, 4));
        return instance.spawn(spawnGroup, MathHelper.ceil(weight * (NoxConfig.increaseGhastSpawns ? 1.5f : 1f)), new SpawnSettings.SpawnEntry(EntityType.GHAST, spawnEntry.minGroupSize(), spawnEntry.maxGroupSize()));
    }

}
