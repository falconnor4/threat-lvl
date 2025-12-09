package com.github.threatlvl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Renders custom nametags with threat indicators for players.
 */
@SideOnly(Side.CLIENT)
public class ThreatRenderer {

    private static final String SKULL = "\u2620"; // ☠

    @SubscribeEvent
    public void onRenderLivingSpecials(RenderLivingEvent.Specials.Pre event) {
        // Only process players
        if (!(event.entity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer target = (EntityPlayer) event.entity;
        EntityPlayer self = Minecraft.getMinecraft().thePlayer;

        // Don't render for self
        if (target == self || self == null) {
            return;
        }

        // Cancel default rendering
        event.setCanceled(true);

        // Render our custom nametag
        renderThreatNametag(target, self, event.x, event.y, event.z);
    }

    /**
     * Render custom nametag with threat indicator.
     */
    private void renderThreatNametag(EntityPlayer target, EntityPlayer self, double x, double y, double z) {
        float threatLevel = ThreatCalculator.calculateThreatLevel(target, self);
        boolean isThreat = ThreatCalculator.isThreat(threatLevel);

        // Color codes: §c = red, §a = green
        String colorCode = isThreat ? "\u00A7c" : "\u00A7a";
        String displayName = colorCode + SKULL + " \u00A7r" + target.getDisplayName().getFormattedText();

        // Get render distance
        double distanceSq = target.getDistanceSqToEntity(self);
        if (distanceSq > 4096.0D) { // 64 blocks squared
            return;
        }

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

        float scale = 0.016666668F * 1.6F;

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + target.height + 0.5F, (float) z);
        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        int textWidth = fontRenderer.getStringWidth(displayName);
        int halfWidth = textWidth / 2;

        // Draw background
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.disableTexture2D();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(-halfWidth - 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos(-halfWidth - 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos(halfWidth + 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos(halfWidth + 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();

        // Draw text
        fontRenderer.drawString(displayName, -halfWidth, 0, 0xFFFFFFFF);

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
