package mcp.mobius.waila.overlay.tooltiprenderers;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.awt.*;

public class TTRenderStack implements IWailaTooltipRenderer {

    @Override
    public Dimension getSize(String[] params, IWailaCommonAccessor accessor) {
        return new Dimension(params[0].equals("1") ? 20 : 16, 16);
    }

    @Override
    public void draw(String[] params, IWailaCommonAccessor accessor) {
        int type = Integer.valueOf(params[0]); //0 for block, 1 for item, 2 for blank space
        if (type == 2)
            return;
        String name = params[1]; //Fully qualified name
        int amount = Integer.valueOf(params[2]);
        int meta = Integer.valueOf(params[3]);

        ItemStack stack = null;
        if (type == 0)
            stack = new ItemStack(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name)), amount, meta);
        if (type == 1)
            stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(name)), amount, meta);

        RenderHelper.enableGUIStandardItemLighting();
        DisplayUtil.renderStack(0, 0, stack);
        RenderHelper.disableStandardItemLighting();
    }

}
