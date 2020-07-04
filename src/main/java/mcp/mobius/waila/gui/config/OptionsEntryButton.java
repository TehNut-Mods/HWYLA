package mcp.mobius.waila.gui.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;

public class OptionsEntryButton extends OptionsListWidget.Entry {

    private final ITextComponent title;
    private final Button button;

    public OptionsEntryButton(ITextComponent title, Button button) {
        this.title = title;
        this.button = button;
        button.func_238482_a_(this.title);
    }

    @Override
    public void func_230432_a_(MatrixStack matrixStack, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
        client.fontRenderer.func_238405_a_(matrixStack, title.getString(), rowLeft + 10, rowTop + (height / 4) + (client.fontRenderer.FONT_HEIGHT / 2), 16777215);
        this.button.field_230690_l_ = rowLeft + 135;
        this.button.field_230691_m_ = rowTop + height / 6;
        this.button.func_230430_a_(matrixStack, mouseX, mouseY, deltaTime);
    }

    @Override
    public boolean func_231044_a_(double mouseY, double mouseX, int button) {
        if (button == 0 && this.button.func_230449_g_()) {
            this.button.func_230988_a_(Minecraft.getInstance().getSoundHandler());
            this.button.func_230930_b_();
            return true;
        }

        return false;
    }
}
