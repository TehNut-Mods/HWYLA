package mcp.mobius.waila.cbcore;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class StackHandler {
	
    public static RenderItem drawItems = Minecraft.getMinecraft().getRenderItem();	
    private static int modelviewDepth = -1;
    private static HashSet<String> stackTraces = new HashSet<String>();	
    private static FontRenderer fontRenderer   = Minecraft.getMinecraft().fontRendererObj;
    
	public static List<String> itemDisplayNameMultiline(ItemStack itemstack, GuiContainer gui, boolean includeHandlers) {
		List<String> namelist = null;
		try {
			namelist = itemstack.getTooltip(Minecraft.getMinecraft().thePlayer, includeHandlers && Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
		} catch (Throwable ignored) {}
		
		if (namelist == null)
				namelist = new ArrayList<String>();
		
		if (namelist.size() == 0)
				namelist.add("Unnamed");
		
		if (namelist.get(0) == null || namelist.get(0).equals(""))
			namelist.set(0, "Unnamed");
		
		/*
		if (includeHandlers) {
			for (IContainerTooltipHandler handler : tooltipHandlers) {
				namelist = handler.handleItemDisplayName(gui, itemstack, namelist);
			}
		}
		*/
		
		namelist.set(0, itemstack.getRarity().rarityColor.toString() + namelist.get(0));
		for (int i = 1; i < namelist.size(); i++)
			namelist.set(i, "\u00a77" + namelist.get(i));
		
		return namelist;
	}
	/**
	* The general name of this item.
	*
	* @param itemstack The {@link ItemStack} to get the name for.
	* @return The first line of the multiline display name.
	*/
	public static String itemDisplayNameShort(ItemStack itemstack) {
		List<String> list = itemDisplayNameMultiline(itemstack, null, false);
		return list.get(0);
	}
	
    public static void drawItem(int i, int j, ItemStack itemstack) {
    	drawItem(i, j, itemstack, getFontRenderer(itemstack));
    }
    
    public static FontRenderer getFontRenderer(ItemStack stack) {
    	if (stack != null && stack.getItem() != null) {
    		FontRenderer f = stack.getItem().getFontRenderer(stack);
    		if (f != null)
    			return f;
    		}
    		return fontRenderer;
    }
    
    public static void drawItem(int i, int j, ItemStack itemstack, FontRenderer fontRenderer) {
        enable3DRender();
        float zLevel = drawItems.zLevel += 100F;
        try {
            drawItems.renderItemAndEffectIntoGUI(itemstack, i, j);
            drawItems.renderItemOverlayIntoGUI(fontRenderer, itemstack, i, j, null);

            if (!checkMatrixStack())
                throw new IllegalStateException("Modelview matrix stack too deep");
            //if (Tessellator.getInstance().getWorldRenderer().isDrawing)
            //	throw new IllegalStateException("Still drawing");
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stackTrace = itemstack + sw.toString();
            if (!stackTraces.contains(stackTrace)) {
                System.err.println("Error while rendering: " + itemstack);
                e.printStackTrace();
                stackTraces.add(stackTrace);
            }

            restoreMatrixStack();
            //if (Tessellator.instance.isDrawing)
            //    Tessellator.getInstance().draw();

            drawItems.zLevel = zLevel;
            drawItems.renderItemIntoGUI(new ItemStack(Blocks.fire), i, j);
        }

        enable2DRender();
        drawItems.zLevel = zLevel - 100;
    }    
    
    public static void enable3DRender() {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public static void enable2DRender() {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    } 	
	
    public static boolean checkMatrixStack() {
        return modelviewDepth < 0 || GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH) == modelviewDepth;
    }    
    
    public static void restoreMatrixStack() {
        if (modelviewDepth >= 0)
            for (int i = GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH); i > modelviewDepth; i--)
                GL11.glPopMatrix();
    }    
    
}
