package mcp.mobius.waila.overlay;

import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.config.FormattingConfig;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static mcp.mobius.waila.api.SpecialChars.*;

public class DisplayUtil {
    private static final String[] NUM_SUFFIXES = new String[]{"", "k", "m", "b", "t"};
    private static final int MAX_LENGTH = 4;
    protected static RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
    private static FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRenderer;

    public static int getDisplayWidth(String s) {
        if (s == null || s.equals(""))
            return 0;

        int width = 0;

        String[] renderSplit = s.split(WailaSplitter);
        for (String render : renderSplit) {
            Matcher renderMatcher = patternRender.matcher(render);
            while (renderMatcher.find()) {
                IWailaTooltipRenderer renderer = ModuleRegistrar.instance().getTooltipRenderer(renderMatcher.group("name"));
                if (renderer != null) {
                    width += renderer.getSize(renderMatcher.group("args").split("\\+,"), DataAccessorCommon.instance).width;
                    s = s.replace(render, "");
                }
            }
        }

        Matcher iconMatcher = patternIcon.matcher(s);
        while (iconMatcher.find())
            width += 8;

        String stripped = stripWailaSymbols(s);
        width += fontRendererObj.getStringWidth(stripped);
        return width;
    }

    public static String stripSymbols(String s) {
        return s.replaceAll(patternRender.pattern(), "").replaceAll(patternMinecraft.pattern(), "").replaceAll(patternWaila.pattern(), "");
    }

    public static String stripWailaSymbols(String s) {
        return s.replaceAll(patternWaila.pattern(), "").replaceAll(patternRender.pattern(), "");
    }

    public static void renderStack(int x, int y, ItemStack stack) {
        enable3DRender();
        try {
            renderItem.renderItemAndEffectIntoGUI(stack, x, y);
            ItemStack overlayRender = stack.copy();
            overlayRender.setCount(1);
            renderItem.renderItemOverlayIntoGUI(fontRendererObj, overlayRender, x, y, null);
            renderStackSize(fontRendererObj, stack, x, y);

        } catch (Exception e) {
            String stackStr = stack != null ? stack.toString() : "NullStack";
            WailaExceptionHandler.handleErr(e, "renderStack | " + stackStr, null);
        }
        enable2DRender();
    }

    public static void renderStackSize(FontRenderer fr, ItemStack stack, int xPosition, int yPosition) {
        if (!stack.isEmpty() && stack.getCount() != 1) {
            String s = shortHandNumber(stack.getCount());

            if (stack.getCount() < 1)
                s = TextFormatting.RED + String.valueOf(stack.getCount());

            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.disableBlend();
//            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            fr.drawStringWithShadow(s, (float) (xPosition + 19 - 2 - fr.getStringWidth(s)), (float) (yPosition + 6 + 3), 16777215);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
        }
    }

    private static String shortHandNumber(Number number) {
        String shorthand = new DecimalFormat("##0E0").format(number);
        shorthand = shorthand.replaceAll("E[0-9]", NUM_SUFFIXES[Character.getNumericValue(shorthand.charAt(shorthand.length() - 1)) / 3]);
        while (shorthand.length() > MAX_LENGTH || shorthand.matches("[0-9]+\\.[a-z]"))
            shorthand = shorthand.substring(0, shorthand.length() - 2) + shorthand.substring(shorthand.length() - 1);

        return shorthand;
    }

    public static void enable3DRender() {
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }

    public static void enable2DRender() {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        float zLevel = 0.0F;

        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos((double) (left + right), (double) top, (double) zLevel).color(f1, f2, f3, f).endVertex();
        buffer.pos((double) left, (double) top, (double) zLevel).color(f1, f2, f3, f).endVertex();
        buffer.pos((double) left, (double) (top + bottom), (double) zLevel).color(f5, f6, f7, f4).endVertex();
        buffer.pos((double) (left + right), (double) (top + bottom), (double) zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height, int tw, int th) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        float zLevel = 0.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos((double) (x), (double) (y + height), (double) zLevel).tex((double) ((float) (textureX) * f), (double) ((float) (textureY + th) * f1)).endVertex();
        buffer.pos((double) (x + width), (double) (y + height), (double) zLevel).tex((double) ((float) (textureX + tw) * f), (double) ((float) (textureY + th) * f1)).endVertex();
        buffer.pos((double) (x + width), (double) (y), (double) zLevel).tex((double) ((float) (textureX + tw) * f), (double) ((float) (textureY) * f1)).endVertex();
        buffer.pos((double) (x), (double) (y), (double) zLevel).tex((double) ((float) (textureX) * f), (double) ((float) (textureY) * f1)).endVertex();
        tessellator.draw();
    }

    public static void drawString(String text, int x, int y, int colour, boolean shadow) {
        if (shadow)
            fontRendererObj.drawStringWithShadow(text, x, y, colour);
        else
            fontRendererObj.drawString(text, x, y, colour);
    }

    public static List<String> itemDisplayNameMultiline(ItemStack itemstack) {
        List<String> namelist = null;
        try {
            namelist = itemstack.getTooltip(Minecraft.getMinecraft().player, ITooltipFlag.TooltipFlags.NORMAL);
        } catch (Throwable ignored) {
        }

        if (namelist == null)
            namelist = new ArrayList<>();

        if (namelist.size() == 0)
            namelist.add("Unnamed");

        if (namelist.get(0) == null || namelist.get(0).equals(""))
            namelist.set(0, "Unnamed");

        namelist.set(0, itemstack.getRarity().rarityColor.toString() + namelist.get(0));
        for (int i = 1; i < namelist.size(); i++)
            namelist.set(i, namelist.get(i));

        return namelist;
    }

    public static String itemDisplayNameShort(ItemStack itemstack) {
        List<String> list = itemDisplayNameMultiline(itemstack);
        return String.format(FormattingConfig.blockFormat, list.get(0));
    }

    public static void renderIcon(int x, int y, int sx, int sy, IconUI icon) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);

        if (icon == null)
            return;

        if (icon.bu != -1)
            DisplayUtil.drawTexturedModalRect(x, y, icon.bu, icon.bv, sx, sy, icon.bsu, icon.bsv);
        DisplayUtil.drawTexturedModalRect(x, y, icon.u, icon.v, sx, sy, icon.su, icon.sv);
    }
}