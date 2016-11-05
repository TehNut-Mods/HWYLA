package mcp.mobius.waila.overlay;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.config.OverlayConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class OverlayRenderer {

    protected static boolean hasBlending;
    protected static boolean hasLight;
    protected static boolean hasDepthTest;
    protected static boolean hasLight0;
    protected static boolean hasLight1;
    protected static boolean hasRescaleNormal;
    protected static boolean hasColorMaterial;
    protected static boolean depthMask;
    protected static int boundTexIndex;
    protected static int depthFunc;

    public static void renderOverlay() {
        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.currentScreen == null &&
                mc.theWorld != null &&
                Minecraft.isGuiEnabled() &&
                !mc.gameSettings.keyBindPlayerList.isKeyDown() &&
                ConfigHandler.instance().showTooltip() &&
                RayTracing.instance().getTarget() != null))
            return;

        if (RayTracing.instance().getTarget().typeOfHit == RayTraceResult.Type.BLOCK && RayTracing.instance().getTargetStack() != null) {
            renderOverlay(WailaTickHandler.instance().tooltip);
        }

        if (RayTracing.instance().getTarget().typeOfHit == RayTraceResult.Type.ENTITY && ConfigHandler.instance().getConfig("general.showents")) {
            renderOverlay(WailaTickHandler.instance().tooltip);
        }
    }

    public static void renderOverlay(Tooltip tooltip) {
        //TrueTypeFont font = (TrueTypeFont)mod_Waila.proxy.getFont();

        GL11.glPushMatrix();
        saveGLState();

        GL11.glScalef(OverlayConfig.scale, OverlayConfig.scale, 1.0f);

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        if (OverlayConfig.alpha != 0)
            drawTooltipBox(tooltip.x, tooltip.y, tooltip.w, tooltip.h, OverlayConfig.bgcolor, OverlayConfig.gradient1, OverlayConfig.gradient2);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        tooltip.draw();
        GL11.glDisable(GL11.GL_BLEND);

        tooltip.draw2nd();

        if (tooltip.hasIcon)
            RenderHelper.enableGUIStandardItemLighting();

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        if (tooltip.hasIcon && tooltip.stack != null && tooltip.stack.getItem() != null) {
//        	System.out.println("" + tooltip.stack.getDisplayName() + tooltip.stack.getMetadata());
            DisplayUtil.renderStack(tooltip.x + 5, tooltip.y + tooltip.h / 2 - 8, tooltip.stack);
        }

        loadGLState();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
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
        GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
    }

    public static void loadGLState() {
        GL11.glDepthMask(depthMask);
        GL11.glDepthFunc(depthFunc);
        if (hasLight) GL11.glEnable(GL11.GL_LIGHTING);
        else GL11.glDisable(GL11.GL_LIGHTING);
        if (hasLight0) GL11.glEnable(GL11.GL_LIGHT0);
        else GL11.glDisable(GL11.GL_LIGHT0);
        if (hasLight1) GL11.glEnable(GL11.GL_LIGHT1);
        else GL11.glDisable(GL11.GL_LIGHT1);
        if (hasDepthTest) GL11.glEnable(GL11.GL_DEPTH_TEST);
        else GL11.glDisable(GL11.GL_DEPTH_TEST);
        if (hasRescaleNormal) GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        else GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        if (hasColorMaterial) GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        else GL11.glDisable(GL11.GL_COLOR_MATERIAL);
        GL11.glPopAttrib();
        //GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawTooltipBox(int x, int y, int w, int h, int bg, int grad1, int grad2) {
        //int bg = 0xf0100010;
        DisplayUtil.drawGradientRect(x + 1, y, w - 1, 1, bg, bg);
        DisplayUtil.drawGradientRect(x + 1, y + h, w - 1, 1, bg, bg);
        DisplayUtil.drawGradientRect(x + 1, y + 1, w - 1, h - 1, bg, bg);//center
        DisplayUtil.drawGradientRect(x, y + 1, 1, h - 1, bg, bg);
        DisplayUtil.drawGradientRect(x + w, y + 1, 1, h - 1, bg, bg);
        //int grad1 = 0x505000ff;
        //int grad2 = 0x5028007F;
        DisplayUtil.drawGradientRect(x + 1, y + 2, 1, h - 3, grad1, grad2);
        DisplayUtil.drawGradientRect(x + w - 1, y + 2, 1, h - 3, grad1, grad2);

        DisplayUtil.drawGradientRect(x + 1, y + 1, w - 1, 1, grad1, grad1);
        DisplayUtil.drawGradientRect(x + 1, y + h - 1, w - 1, 1, grad2, grad2);
    }
}