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

package net.scirave.nox.util;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.registry.Registry;
import net.minecraft.world.LocalDifficulty;
import net.scirave.nox.Nox;
import net.scirave.nox.config.NoxConfig;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;

public class NoxUtil {

    public static final TagKey<Block> NOX_ALWAYS_MINE = TagKey.of(RegistryKeys.BLOCK, new Identifier("nox:always_mine"));
    public static final TagKey<Block> NOX_CANT_MINE = TagKey.of(RegistryKeys.BLOCK, new Identifier("nox:cant_mine"));
    public static final TagKey<Item> FIREPROOF = TagKey.of(RegistryKeys.ITEM, new Identifier("nox:fireproof"));
    public static final TagKey<Item> ARMOR = TagKey.of(RegistryKeys.ITEM, new Identifier("nox:mob_armor"));
    public static final TagKey<Item> TOOLS = TagKey.of(RegistryKeys.ITEM, new Identifier("nox:mob_tools"));
    private final static ItemStack WOOD_PICKAXE = Items.WOODEN_PICKAXE.getDefaultStack();
    private final static ItemStack WOOD_AXE = Items.WOODEN_AXE.getDefaultStack();
    private final static ItemStack WOOD_SHOVEL = Items.WOODEN_SHOVEL.getDefaultStack();

    public static boolean isAtWoodLevel(BlockState state) {
        return !state.isToolRequired() || WOOD_PICKAXE.isSuitableFor(state) || WOOD_AXE.isSuitableFor(state) || WOOD_SHOVEL.isSuitableFor(state);
    }

    public static boolean isAnAlly(MobEntity attacker, MobEntity victim) {

        boolean validTypes = (attacker instanceof Monster && victim instanceof Monster) ||
                (attacker instanceof GolemEntity && victim instanceof GolemEntity);

        LivingEntity attackerTarget = attacker.getTarget();
        LivingEntity victimTarget = victim.getTarget();

        return NoxConfig.noFriendlyFire && validTypes && attackerTarget != attacker && victimTarget != victim && victimTarget != null && attackerTarget == victimTarget;
    }

    public static void EnderDragonShootFireball(EnderDragonEntity dragon, LivingEntity target) {
        Vec3d i = dragon.getRotationVec(1.0F);
        double k = dragon.head.getX() - i.x;
        double l = dragon.head.getBodyY(0.5D) + 0.5D;
        double m = dragon.head.getZ() - i.z;
        double n = target.getX() - k;
        double o = target.getBodyY(0.5D) - l;
        double p = target.getZ() - m;
        if (!dragon.isSilent()) {
            dragon.getWorld().syncWorldEvent(null, 1017, dragon.getBlockPos(), 0);
        }

        DragonFireballEntity dragonFireballEntity = new DragonFireballEntity(dragon.getWorld(), dragon, n, o, p);
        dragonFireballEntity.refreshPositionAndAngles(k, l, m, 0.0F, 0.0F);
        dragonFireballEntity.powerX *= 5;
        dragonFireballEntity.powerY *= 5;
        dragonFireballEntity.powerZ *= 5;
        dragon.getWorld().spawnEntity(dragonFireballEntity);
    }

    public static Item randomWeapon(Random random) {
        return Registries.ITEM.getOrCreateEntryList(TOOLS).getRandom(random).map(RegistryEntry::value).orElse(Items.AIR);
    }

    public static Item randomArmor(Random random) {
        return Registries.ITEM.getOrCreateEntryList(ARMOR).getRandom(random).map(RegistryEntry::value).orElse(Items.AIR);
    }

    public static double getLeewayAmount(double damage, double total, int armor, double toughness, double modifier, DamageSource source) {
        double diff = damage * modifier - DamageUtil.getDamageLeft((float) total, source, armor, (float) toughness);
        double ratio = 0;
        if (diff != 0) {
            ratio = diff / damage;
        }

        return ratio;
    }

    public static boolean resistanceWithinLeeway(double damage, double total, int armor, double toughness, double lowerLeeway, double higherLeeway, double modifier, DamageSource source) {
        double ratio = getLeewayAmount(damage, total, armor, toughness, modifier, source);
        return ratio >= -higherLeeway && ratio <= lowerLeeway;
    }

    public static double getItemQuality(Item item, EquipmentSlot slot, RegistryEntry<EntityAttribute> type, @Nullable Double base) {
        if (base == null) {
            base = (double) 0;
        }

        var map = item.getAttributeModifiers();
        double multiple = 0;
        double multiply = 1;
        double add = 0;

        for (var entry : map.modifiers()) {
            var attribute = entry.attribute();
            var modifier = entry.modifier();

            if (attribute == type && entry.slot().matches(slot)) {
                switch (modifier.operation()) {
                    case ADD_MULTIPLIED_BASE -> multiple += modifier.value();
                    case ADD_VALUE -> add += modifier.value();
                    case ADD_MULTIPLIED_TOTAL -> multiply *= 1 + modifier.value();
                }

            }
        }

        return (base + add + (base + add) * multiple) * (multiply);
    }

    public static double getItemDamage(Item item, EquipmentSlot slot, double baseDamage) {
        return getItemQuality(item, slot, EntityAttributes.GENERIC_ATTACK_DAMAGE, baseDamage);
    }

    public static Pair<Integer, Float> getItemProtection(Item item, EquipmentSlot slot, double baseDamage) {
        int armor = (int) Math.floor(getItemQuality(item, slot, EntityAttributes.GENERIC_ARMOR, (double) 0));
        float toughness = (float) getItemQuality(item, slot, EntityAttributes.GENERIC_ARMOR_TOUGHNESS, (double) 0);

        return new Pair<>(armor, toughness);
    }

    public static double getItemDPS(Item item, EquipmentSlot slot, double baseDamage, double attackSpeed) {
        return getItemQuality(item, slot, EntityAttributes.GENERIC_ATTACK_DAMAGE, baseDamage) * getItemQuality(item, slot, EntityAttributes.GENERIC_ATTACK_SPEED, attackSpeed);
    }

    public static double getBestPlayerDPS(PlayerEntity player) {
        double defaultAttackSpeed = player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED);
        double damage = 0;
        PlayerInventory inventory = player.getInventory();

        for (int i = 0; i < 8; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                double potential = getItemDPS(stack.getItem(), EquipmentSlot.MAINHAND, 1, defaultAttackSpeed);
                if (potential > damage) {
                    damage = potential;
                }
            }
        }

        ItemStack stack = inventory.getStack(PlayerInventory.OFF_HAND_SLOT);
        if (!stack.isEmpty()) {
            double potential = getItemDPS(stack.getItem(), EquipmentSlot.MAINHAND, 1, defaultAttackSpeed);
            if (potential > damage) {
                damage = potential;
            }
        }

        return damage;
    }

    public static void weaponRoulette(ServerWorld world, MobEntity mob, Random random, LocalDifficulty difficulty) {
        PlayerEntity player = world.getClosestPlayer(mob.getX(), mob.getY(), mob.getZ(), 128, true);
        if (player != null) {
            int luck = MathHelper.nextInt(random, 1, 4);
            boolean freeFirstPass = luck == 1;
            boolean noWeapon = luck == 4;

            if (noWeapon) return;

            int armor = player.getArmor();
            float toughness = (float) mob.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);

            double damage = mob.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            double total = damage;

            double mod = player.getMaxHealth() / Math.max(mob.getMaxHealth(), 20);
            float clamped = difficulty.getClampedLocalDifficulty();

            double lowerLeeway = 0.50;
            lowerLeeway -= lowerLeeway * 0.5 * clamped;

            double higherLeeway = 0.25;
            higherLeeway += higherLeeway * 4 * clamped;

            Item item = null;
            int iterated = 0;

            while (freeFirstPass || !resistanceWithinLeeway(damage, total, armor, toughness, lowerLeeway, higherLeeway, mod, world.getDamageSources().generic()) && iterated < 20) {
                freeFirstPass = false;
                for (int i = 0; i < 5; i++) {
                    item = randomWeapon(random);

                    total = getItemDamage(item, EquipmentSlot.MAINHAND, damage);

                    if (resistanceWithinLeeway(damage, total, armor, toughness, lowerLeeway, higherLeeway, mod, world.getDamageSources().generic())) {
                        break;
                    }
                }

                lowerLeeway += 0.1;
                higherLeeway += 0.1;
                iterated++;
            }

            if (item != null) {
                ItemStack stack = item.getDefaultStack();
                mob.equipStack(EquipmentSlot.MAINHAND, stack);
            }
        }
    }

    public static void armorRoulette(ServerWorld world, MobEntity mob, Random random, LocalDifficulty difficulty) {
        PlayerEntity player = world.getClosestPlayer(mob.getX(), mob.getY(), mob.getZ(), 64, true);
        if (player != null) {
            int luck = MathHelper.nextInt(random, 1, 4);
            boolean freeFirstPass = luck == 1;
            boolean noArmor = luck == 4;

            if (noArmor) return;

            double modifier = Math.max(mob.getMaxHealth(), 20) / player.getMaxHealth();
            double damage = 4 * 2;
            double total = Math.max((getBestPlayerDPS(player) / 3) * 2, damage);

            int armor = 0;
            float toughness = 0;

            float clamped = difficulty.getClampedLocalDifficulty();

            double lowerLeeway = 0.25;
            lowerLeeway += lowerLeeway * 4 * clamped;

            double higherLeeway = 0.50;
            higherLeeway -= higherLeeway * 0.5 * clamped;

            HashSet<ArmorItem> armorItems = new HashSet<>();
            int iterated = 0;

            while (freeFirstPass || !resistanceWithinLeeway(damage, total, armor, toughness, lowerLeeway, higherLeeway, modifier, world.getDamageSources().generic()) && iterated < 20) {
                freeFirstPass = false;
                for (int i = 0; i < 5; i++) {
                    double lastLeeway = getLeewayAmount(damage, total, armor, toughness, modifier, world.getDamageSources().generic());
                    Item item = randomArmor(random);

                    if (item instanceof ArmorItem armorItem) {
                        EquipmentSlot slot = armorItem.getSlotType();

                        ArmorItem toRemove = null;
                        for (ArmorItem potential : armorItems) {
                            if (potential.getSlotType() == slot) {
                                toRemove = potential;
                                break;
                            }
                        }

                        if (toRemove != null) {
                            armor -= toRemove.getProtection();
                            toughness -= toRemove.getToughness();
                        }

                        armor += armorItem.getProtection();
                        toughness += armorItem.getToughness();

                        double newLeeway = getLeewayAmount(damage, total, armor, toughness, modifier, world.getDamageSources().generic());
                        if ((newLeeway <= 0 && (newLeeway - lastLeeway) >= 0)
                                || resistanceWithinLeeway(damage, total, armor, toughness, lowerLeeway, higherLeeway, modifier, world.getDamageSources().generic())) {
                            if (toRemove != null) {
                                armorItems.remove(toRemove);
                            }
                            armorItems.add(armorItem);
                        } else {
                            armor -= armorItem.getProtection();
                            toughness -= armorItem.getToughness();
                            if (toRemove != null) {
                                armor += toRemove.getProtection();
                                toughness += toRemove.getToughness();
                            }
                        }
                    }

                    if (resistanceWithinLeeway(damage, total, armor, toughness, lowerLeeway, higherLeeway, modifier, world.getDamageSources().generic())) {
                        break;
                    }
                }

                lowerLeeway += 0.1;
                higherLeeway += 0.1;
                iterated++;
            }

            if (!armorItems.isEmpty()) {
                for (ArmorItem item : armorItems) {
                    mob.equipStack(item.getSlotType(), item.getDefaultStack());
                }
            }
        }
    }

}
