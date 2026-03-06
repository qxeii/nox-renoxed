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
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.scirave.nox.config.NoxConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(DefaultBiomeFeatures.class)
public class DefaultBiomeFeaturesMixin {

    @ModifyArgs(method = "addMonsters", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/SpawnSettings$Builder;spawn(Lnet/minecraft/entity/SpawnGroup;ILnet/minecraft/world/biome/SpawnSettings$SpawnEntry;)Lnet/minecraft/world/biome/SpawnSettings$Builder;", ordinal = 7))
    private static void nox$witchIncreasedSpawn(Args args) {
        if (NoxConfig.doMoreWitchSpawns) {
            args.set(1, ((int) args.get(1)) * 3);
            SpawnSettings.SpawnEntry entry = args.get(2);
            args.set(2, new SpawnSettings.SpawnEntry(entry.type(), entry.minGroupSize(), entry.maxGroupSize() * 3));
        }
    }

    @ModifyArgs(method = "addMonsters", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/SpawnSettings$Builder;spawn(Lnet/minecraft/entity/SpawnGroup;ILnet/minecraft/world/biome/SpawnSettings$SpawnEntry;)Lnet/minecraft/world/biome/SpawnSettings$Builder;", ordinal = 5))
    private static void nox$slimeDecreasedSpawn(Args args) {
        if (NoxConfig.slimeNaturalSpawn) {
            args.set(1, (int) Math.floor(((int) args.get(1)) / 2));
            SpawnSettings.SpawnEntry entry = args.get(2);
            args.set(2, new SpawnSettings.SpawnEntry(entry.type(), entry.minGroupSize() / 4, entry.maxGroupSize() / 2));
        }
    }

    @ModifyArgs(method = "addOceanMobs", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/SpawnSettings$Builder;spawn(Lnet/minecraft/entity/SpawnGroup;ILnet/minecraft/world/biome/SpawnSettings$SpawnEntry;)Lnet/minecraft/world/biome/SpawnSettings$Builder;", ordinal = 2))
    private static void nox$drownedIncreasedSpawn1(Args args) {
        if (NoxConfig.doMoreDrownedSpawns) {
            args.set(1, ((int) args.get(1)) * 8);
            SpawnSettings.SpawnEntry entry = args.get(2);
            args.set(2, new SpawnSettings.SpawnEntry(entry.type(), entry.minGroupSize() * 4, entry.maxGroupSize() * 8));
        }
    }

    @ModifyArgs(method = "addWarmOceanMobs", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/SpawnSettings$Builder;spawn(Lnet/minecraft/entity/SpawnGroup;ILnet/minecraft/world/biome/SpawnSettings$SpawnEntry;)Lnet/minecraft/world/biome/SpawnSettings$Builder;", ordinal = 3))
    private static void nox$drownedIncreasedSpawn2(Args args) {
        if (NoxConfig.doMoreDrownedSpawns) {
            args.set(1, ((int) args.get(1)) * 8);
            SpawnSettings.SpawnEntry entry = args.get(2);
            args.set(2, new SpawnSettings.SpawnEntry(entry.type(), entry.minGroupSize() * 4, entry.maxGroupSize() * 8));
        }
    }

    @Inject(method = "addCaveMobs", at = @At("TAIL"))
    private static void nox$caveSpiderSpawns(SpawnSettings.Builder builder, CallbackInfo ci) {
        if (NoxConfig.spawnCaveSpidersInCaves)
            builder.spawn(SpawnGroup.MONSTER, 80, new SpawnSettings.SpawnEntry(EntityType.CAVE_SPIDER, 4, 4));
    }

    @Inject(method = "addOceanMobs", at = @At("TAIL"))
    private static void nox$guardianSpawns1(SpawnSettings.Builder builder, int squidWeight, int squidMaxGroupSize, int codWeight, CallbackInfo ci) {
        if (NoxConfig.guardianNaturalSpawnWeight > 0)
            builder.spawn(SpawnGroup.MONSTER, NoxConfig.guardianNaturalSpawnWeight, new SpawnSettings.SpawnEntry(EntityType.GUARDIAN, 4, 4));
    }

    @Inject(method = "addWarmOceanMobs", at = @At("TAIL"))
    private static void nox$guardianSpawns2(SpawnSettings.Builder builder, int squidWeight, int squidMinGroupSize, CallbackInfo ci) {
        if (NoxConfig.guardianNaturalSpawnWeight > 0)
            builder.spawn(SpawnGroup.MONSTER, NoxConfig.guardianNaturalSpawnWeight, new SpawnSettings.SpawnEntry(EntityType.GUARDIAN, 4, 4));
    }

}
