package mcp.mobius.waila.overlay.tooltiprenderers;

import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.ITooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.IconUI;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.Dimension;

public class TooltipRendererIcon implements ITooltipRenderer {

    private final String type;
    private final int size = 8;

    public TooltipRendererIcon(String type) {
        this.type = type;
    }

    @Override
    public Dimension getSize(CompoundTag tag, ICommonAccessor accessor) {
        return new Dimension(size, size);
    }

    @Override
    public void draw(MatrixStack matrices, CompoundTag tag, ICommonAccessor accessor, int x, int y) {
        DisplayUtil.renderIcon(x, y, size, size, IconUI.bySymbol(type));
    }

}
