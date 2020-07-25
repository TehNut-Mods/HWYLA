package mcp.mobius.waila.overlay;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.addons.core.PluginCore;
import mcp.mobius.waila.api.event.WailaRenderEvent;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.Rectangle;

public class OverlayRenderer {

    protected static boolean hasLight;
    protected static boolean hasDepthTest;
    protected static boolean hasLight0;
    protected static boolean hasLight1;
    protected static boolean hasRescaleNormal;
    protected static boolean hasColorMaterial;
    protected static boolean depthMask;
    protected static int depthFunc;

    public static void renderOverlay() {
        if (WailaTickHandler.instance().tooltip == null)
            return;

        if (!Waila.CONFIG.get().getGeneral().shouldDisplayTooltip())
            return;

        if (Waila.CONFIG.get().getGeneral().getDisplayMode() == WailaConfig.DisplayMode.HOLD_KEY && !WailaClient.showOverlay.getKeyBinding().isPressed())
            return;

        Minecraft mc = Minecraft.getInstance();
        if ((mc.currentScreen != null && mc.gameSettings.chatVisibility != ChatVisibility.HIDDEN) || mc.world == null)
            return;

        assert mc.player != null;
        boolean isOnServer = !mc.isSingleplayer() || mc.player.connection.getPlayerInfoMap().size() > 1;
        if (Waila.CONFIG.get().getGeneral().shouldHideFromPlayerList() && mc.gameSettings.keyBindPlayerList.isPressed() && isOnServer)
            return;

        if (!Minecraft.isGuiEnabled())
            return;

        if (mc.gameSettings.showDebugInfo && Waila.CONFIG.get().getGeneral().shouldHideFromDebug())
            return;

        if (RayTracing.INSTANCE.getTarget() == null)
            return;

        if (RayTracing.INSTANCE.getTarget().getType() == RayTraceResult.Type.BLOCK && !RayTracing.INSTANCE.getTargetStack().isEmpty())
            renderOverlay(WailaTickHandler.instance().tooltip);

        if (RayTracing.INSTANCE.getTarget().getType() == RayTraceResult.Type.ENTITY && PluginConfig.INSTANCE.get(PluginCore.CONFIG_SHOW_ENTITY))
            renderOverlay(WailaTickHandler.instance().tooltip);
    }

    public static void enableGUIStandardItemLighting() {
        RenderSystem.pushMatrix();
        RenderSystem.rotatef(-30.0F, 0.0F, 1.0F, 0.0F);
        RenderSystem.rotatef(165.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        RenderSystem.popMatrix();
    }

    public static void renderOverlay(Tooltip tooltip) {
        Minecraft.getInstance().getProfiler().startSection("Waila Overlay");
        RenderSystem.pushMatrix();
        saveGLState();

        RenderSystem.scalef(Waila.CONFIG.get().getOverlay().getOverlayScale(), Waila.CONFIG.get().getOverlay().getOverlayScale(), 1.0F);

        RenderSystem.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();

        WailaRenderEvent.Pre preEvent = new WailaRenderEvent.Pre(DataAccessor.INSTANCE, tooltip.getPosition());
        if (MinecraftForge.EVENT_BUS.post(preEvent)) {
            enableGUIStandardItemLighting();
            RenderSystem.enableRescaleNormal();
            loadGLState();
            RenderSystem.enableDepthTest();
            RenderSystem.popMatrix();
            return;
        }

        Rectangle position = preEvent.getPosition();
        WailaConfig.ConfigOverlay.ConfigOverlayColor color = Waila.CONFIG.get().getOverlay().getColor();
        WailaRenderEvent.Color colorEvent = new WailaRenderEvent.Color(color.getAlpha(), color.getBackgroundColor(), color.getGradientStart(), color.getGradientEnd());
        MinecraftForge.EVENT_BUS.post(colorEvent);
        drawTooltipBox(position.x, position.y, position.width, position.height, colorEvent.getBackground(), colorEvent.getGradientStart(), colorEvent.getGradientEnd());

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        tooltip.draw();
        RenderSystem.disableBlend();

        if (tooltip.hasItem())
            enableGUIStandardItemLighting();

        RenderSystem.enableRescaleNormal();
        if (tooltip.hasItem())
            DisplayUtil.renderStack(position.x + 5, position.y + position.height / 2 - 8, RayTracing.INSTANCE.getIdentifierStack());

        WailaRenderEvent.Post postEvent = new WailaRenderEvent.Post(position);
        MinecraftForge.EVENT_BUS.post(postEvent);

        loadGLState();
        RenderSystem.enableDepthTest();
        RenderSystem.popMatrix();
        Minecraft.getInstance().getProfiler().endSection();
    }

    public static void saveGLState() {
        hasLight = GL11.glGetBoolean(GL11.GL_LIGHTING);
        hasLight0 = GL11.glGetBoolean(GL11.GL_LIGHT0);
        hasLight1 = GL11.glGetBoolean(GL11.GL_LIGHT1);
        hasDepthTest = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
        hasRescaleNormal = GL11.glGetBoolean(GL12.GL_RESCALE_NORMAL);
        hasColorMaterial = GL11.glGetBoolean(GL11.GL_COLOR_MATERIAL);
        depthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        GL11.glPushAttrib(GL11.GL_CURRENT_BIT); // Leave me alone :(
    }

    public static void loadGLState() {
        RenderSystem.depthMask(depthMask);
        RenderSystem.depthFunc(depthFunc);
        if (hasLight)
            RenderSystem.enableLighting();
        else
            RenderSystem.disableLighting();

        if (hasLight0)
            GlStateManager.enableLight(0);
        else
            //GlStateManager.disableLight(0);

        if (hasLight1)
            GlStateManager.enableLight(1);
        else
            //GlStateManager.disableLight(1);

        if (hasDepthTest)
            RenderSystem.enableDepthTest();
        else
            RenderSystem.disableDepthTest();
        if (hasRescaleNormal)
            RenderSystem.enableRescaleNormal();
        else
            RenderSystem.disableRescaleNormal();
        if (hasColorMaterial)
            RenderSystem.enableColorMaterial();
        else
            RenderSystem.disableColorMaterial();

        RenderSystem.popAttributes();
    }

    public static void drawTooltipBox(int x, int y, int w, int h, int bg, int grad1, int grad2) {
        DisplayUtil.drawGradientRect(x + 1, y, w - 1, 1, bg, bg);
        DisplayUtil.drawGradientRect(x + 1, y + h, w - 1, 1, bg, bg);
        DisplayUtil.drawGradientRect(x + 1, y + 1, w - 1, h - 1, bg, bg);//center
        DisplayUtil.drawGradientRect(x, y + 1, 1, h - 1, bg, bg);
        DisplayUtil.drawGradientRect(x + w, y + 1, 1, h - 1, bg, bg);
        DisplayUtil.drawGradientRect(x + 1, y + 2, 1, h - 3, grad1, grad2);
        DisplayUtil.drawGradientRect(x + w - 1, y + 2, 1, h - 3, grad1, grad2);

        DisplayUtil.drawGradientRect(x + 1, y + 1, w - 1, 1, grad1, grad1);
        DisplayUtil.drawGradientRect(x + 1, y + h - 1, w - 1, 1, grad2, grad2);
    }
}