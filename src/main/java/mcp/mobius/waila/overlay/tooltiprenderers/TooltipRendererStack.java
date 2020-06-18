package mcp.mobius.waila.overlay.tooltiprenderers;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.ITooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.Dimension;

public class TooltipRendererStack implements ITooltipRenderer {

    @Override
    public Dimension getSize(CompoundTag tag, ICommonAccessor accessor) {
        return new Dimension(18, 18);
    }

    @Override
    public void draw(MatrixStack matrices, CompoundTag tag, ICommonAccessor accessor, int x, int y) {
        int count = tag.getInt("count");
        if (count <= 0)
            return;

        Item item = Registry.ITEM.get(new Identifier(tag.getString("id")));
        if (item == Items.AIR)
            return;

        CompoundTag stackTag = null;
        try {
            stackTag = StringNbtReader.parse(tag.getString("nbt"));
        } catch (CommandSyntaxException e) {
            // No-op
        }

        ItemStack stack = new ItemStack(item, count);
        if (stackTag != null)
            stack.setTag(stackTag);

        DisplayUtil.renderStack(matrices, x, y, stack);
    }

}
