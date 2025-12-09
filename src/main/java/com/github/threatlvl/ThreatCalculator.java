package com.github.threatlvl;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * Calculates threat level of a target player relative to the local player.
 * Takes into account health, armor, and enchantments.
 * 
 * NOTE: This only uses data that is already synced to the client for rendering
 * purposes (visible armor, held items, entity state). No server-only data
 * access.
 */
public class ThreatCalculator {

    // Weights for threat calculation
    private static final float HEALTH_WEIGHT = 0.35f;
    private static final float ARMOR_WEIGHT = 0.35f;
    private static final float ENCHANT_WEIGHT = 0.30f;

    /**
     * Calculate the threat level of a target player relative to self.
     * 
     * @param target The player to evaluate
     * @param self   The local player
     * @return float from 0.0 (no threat) to 1.0+ (high threat)
     */
    public static float calculateThreatLevel(EntityPlayer target, EntityPlayer self) {
        float healthScore = calculateHealthScore(target, self);
        float armorScore = calculateArmorScore(target, self);
        float enchantScore = calculateEnchantmentScore(target, self);

        return (healthScore * HEALTH_WEIGHT) + (armorScore * ARMOR_WEIGHT) + (enchantScore * ENCHANT_WEIGHT);
    }

    /**
     * Compare health ratios. Higher target health = more threat.
     */
    private static float calculateHealthScore(EntityPlayer target, EntityPlayer self) {
        float targetHealth = target.getHealth() + target.getAbsorptionAmount();
        float selfHealth = self.getHealth() + self.getAbsorptionAmount();

        if (selfHealth <= 0)
            return 1.0f; // Avoid division by zero
        return targetHealth / selfHealth;
    }

    /**
     * Compare armor values. Uses total armor points from equipment.
     */
    private static float calculateArmorScore(EntityPlayer target, EntityPlayer self) {
        int targetArmor = getTotalArmorValue(target);
        int selfArmor = getTotalArmorValue(self);

        // Normalize: max armor is 20 (full diamond)
        float targetNorm = targetArmor / 20.0f;
        float selfNorm = selfArmor / 20.0f;

        if (selfNorm <= 0) {
            return targetNorm > 0 ? 1.5f : 0.5f; // They have armor, we don't
        }
        return targetNorm / selfNorm;
    }

    /**
     * Get total armor points from all equipped armor pieces.
     */
    private static int getTotalArmorValue(EntityPlayer player) {
        int total = 0;
        for (int i = 0; i < 4; i++) {
            ItemStack armorStack = player.getCurrentArmor(i);
            if (armorStack != null && armorStack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) armorStack.getItem();
                total += armor.damageReduceAmount;
            }
        }
        return total;
    }

    /**
     * Compare enchantment strength. Considers Protection and damage enchants.
     */
    private static float calculateEnchantmentScore(EntityPlayer target, EntityPlayer self) {
        int targetEnchantPower = getEnchantmentPower(target);
        int selfEnchantPower = getEnchantmentPower(self);

        // Normalize to a reasonable scale (max ~40 for full prot 4 + sharp 5)
        float targetNorm = targetEnchantPower / 40.0f;
        float selfNorm = selfEnchantPower / 40.0f;

        if (selfNorm <= 0) {
            return targetNorm > 0 ? 1.5f : 0.5f;
        }
        return targetNorm / selfNorm;
    }

    /**
     * Calculate total enchantment power from armor (protection) and held item
     * (damage).
     */
    private static int getEnchantmentPower(EntityPlayer player) {
        int power = 0;

        // Check armor for protection enchantments
        for (int i = 0; i < 4; i++) {
            ItemStack armorStack = player.getCurrentArmor(i);
            if (armorStack != null) {
                // Protection (ID 0), Fire Protection (ID 1), Blast Protection (ID 3),
                // Projectile Protection (ID 4)
                power += EnchantmentHelper.getEnchantmentLevel(0, armorStack); // Protection
                power += EnchantmentHelper.getEnchantmentLevel(1, armorStack); // Fire Protection
                power += EnchantmentHelper.getEnchantmentLevel(3, armorStack); // Blast Protection
                power += EnchantmentHelper.getEnchantmentLevel(4, armorStack); // Projectile Protection
            }
        }

        // Check held item for damage enchantments
        ItemStack heldItem = player.getHeldItem();
        if (heldItem != null) {
            power += EnchantmentHelper.getEnchantmentLevel(16, heldItem) * 2; // Sharpness (weighted more)
            power += EnchantmentHelper.getEnchantmentLevel(17, heldItem); // Smite
            power += EnchantmentHelper.getEnchantmentLevel(18, heldItem); // Bane of Arthropods
            power += EnchantmentHelper.getEnchantmentLevel(19, heldItem); // Knockback
            power += EnchantmentHelper.getEnchantmentLevel(20, heldItem) * 2; // Fire Aspect (weighted more)
        }

        return power;
    }

    /**
     * Determine if target is a threat (stronger than self).
     * 
     * @param threatLevel The calculated threat level
     * @return true if threat level > 1.0 (target is stronger)
     */
    public static boolean isThreat(float threatLevel) {
        return threatLevel > 1.0f;
    }
}
