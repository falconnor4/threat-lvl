package com.github.threatlvl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Modifies player display names to include threat indicators.
 * Uses NameFormat event to prepend skull - doesn't interfere with other mods.
 */
@SideOnly(Side.CLIENT)
public class ThreatRenderer {

    private static final String SKULL = "\u2620"; // ☠

    @SubscribeEvent
    public void onNameFormat(PlayerEvent.NameFormat event) {
        EntityPlayer target = event.entityPlayer;
        EntityPlayer self = Minecraft.getMinecraft().thePlayer;

        // Don't modify our own name or if we're not in-game yet
        if (self == null || target == self) {
            return;
        }

        // Calculate threat
        float threatLevel = ThreatCalculator.calculateThreatLevel(target, self);

        // Color codes: §c = red (threat), §e = yellow (equal), §a = green (weak)
        String colorCode;
        if (threatLevel > 1.2f) {
            colorCode = "\u00A7c"; // Red - stronger than you
        } else if (threatLevel >= 0.8f) {
            colorCode = "\u00A7e"; // Yellow - about equal
        } else {
            colorCode = "\u00A7a"; // Green - weaker than you
        }

        // Prepend skull to display name
        event.displayname = colorCode + SKULL + " \u00A7r" + event.displayname;
    }
}
