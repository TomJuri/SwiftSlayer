package dev.macrohq.swiftslayer.util;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static dev.macrohq.swiftslayer.SwiftSlayer.mc;

public class RenderUtil {
    public static List<BlockPos> markers = new ArrayList<>();
    public static List<BlockPos> filledBox = new ArrayList<>();
    Color green = new Color(0,255,0,255);

    @SubscribeEvent
    void renderWorldLastEvent(RenderWorldLastEvent event){
        Color chromaHSB = Color.getHSBColor((float) ((System.currentTimeMillis() / 10) % 2000) / 2000, 1, 1);
        Color chroma = new Color(chromaHSB.getRed(), chromaHSB.getGreen(), chromaHSB.getBlue(), 120);
        for(BlockPos block: filledBox){
            drawFilledBox(event, block, chroma, true);
        }
        for(BlockPos block: markers){
            drawBox(event, block, green, true);
        }
    }

    public void drawFilledBox(RenderWorldLastEvent event, BlockPos blockPos, Color color, boolean esp){
        AxisAlignedBB aabb = new AxisAlignedBB(blockPos.getX()-0.01, blockPos.getY()-0.01, blockPos.getZ()-0.01, blockPos.getX()+1+0.01, blockPos.getY()+1+0.01,blockPos.getZ()+1+0.01);
        drawFilledBox(event, aabb, color, esp);
    }

    public void drawBox(RenderWorldLastEvent event, BlockPos blockPos, Color color, boolean esp){
        AxisAlignedBB aabb = new AxisAlignedBB(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX()+1, blockPos.getY()+1,blockPos.getZ()+1);
        drawBox(event, aabb, color, esp);
    }

    public void drawBox(RenderWorldLastEvent event, AxisAlignedBB aabb, Color color, boolean esp) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferBuilder = tessellator.getWorldRenderer();
        final Entity render = mc.getRenderViewEntity();

        final double realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * event.partialTicks;
        final double realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * event.partialTicks;
        final double realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * event.partialTicks;

        float r = color.getRed()/255.0f;
        float g = color.getGreen()/255.0f;
        float b = color.getBlue()/255.0f;
        float a = 255.0f * 0.9f;

        GlStateManager.pushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GlStateManager.translate(-realX, -realY, -realZ);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GL11.glDisable(3553);
        GL11.glLineWidth(3f);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        if(esp){
            GlStateManager.disableDepth();
        }
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r,g,b,a).endVertex();

        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r,g,b,a).endVertex();

        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r,g,b,a).endVertex();
//
        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r,g,b,a).endVertex();

        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r,g,b,a).endVertex();

        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r,g,b,a).endVertex();

        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r,g,b,a).endVertex();

        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r,g,b,a).endVertex();

        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r,g,b,a).endVertex();

        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r,g,b,a).endVertex();

        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r,g,b,a).endVertex();

        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r,g,b,a).endVertex();
        tessellator.draw();

        GlStateManager.translate(realX, realY, realZ);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        if(esp){
            GlStateManager.enableDepth();
        }
        GlStateManager.depthMask(true);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }

    public void drawFilledBox(RenderWorldLastEvent event, AxisAlignedBB aabb, Color color, boolean esp) {
        final Entity render = mc.getRenderViewEntity();

        final double realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * event.partialTicks;
        final double realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * event.partialTicks;
        final double realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * event.partialTicks;

        float r = color.getRed()/255.0f;
        float g = color.getGreen()/255.0f;
        float b = color.getBlue()/255.0f;
        float a = color.getAlpha() / 255.0f * 0.3f;

        GlStateManager.pushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GlStateManager.translate(-realX, -realY, -realZ);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GL11.glDisable(3553);
        GL11.glLineWidth(3f);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        if(esp) {
            GlStateManager.disableDepth();
        }
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferBuilder = tessellator.getWorldRenderer();


        GlStateManager.color(r,g,b,a);
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r,g,b,a).endVertex();
        tessellator.draw();

        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r,g,b,a).endVertex();
        tessellator.draw();

        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r,g,b,a).endVertex();
        tessellator.draw();

        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r,g,b,a).endVertex();
        tessellator.draw();

        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r,g,b,a).endVertex();
        tessellator.draw();

        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r,g,b,a).endVertex();
        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r,g,b,a).endVertex();
        tessellator.draw();

        GlStateManager.translate(realX, realY, realZ);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        if(esp) {
            GlStateManager.enableDepth();
        }
        GlStateManager.depthMask(true);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }
    public void drawLine(RenderWorldLastEvent event, Vec3 blockPos1, Vec3 blockPos2, Color color) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferBuilder = tessellator.getWorldRenderer();
        final Entity render = mc.getRenderViewEntity();

        final double realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * event.partialTicks;
        final double realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * event.partialTicks;
        final double realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * event.partialTicks;

        float r = color.getRed()/255.0f;
        float g = color.getGreen()/255.0f;
        float b = color.getBlue()/255.0f;
        float a = 255.0f * 0.9f;

        GlStateManager.pushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GlStateManager.translate(-realX, -realY, -realZ);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GL11.glDisable(3553);
        GL11.glLineWidth(3f);
//        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(blockPos1.xCoord, blockPos1.yCoord,blockPos1.zCoord).color(r,g,b,a).endVertex();
        bufferBuilder.pos(blockPos2.xCoord, blockPos2.yCoord, blockPos2.zCoord).color(r,g,b,a).endVertex();
        tessellator.draw();

        GlStateManager.translate(realX, realY, realZ);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
//        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }
}
