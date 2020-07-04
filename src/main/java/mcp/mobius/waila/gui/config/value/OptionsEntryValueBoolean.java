package mcp.mobius.waila.gui.config.value;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.function.Consumer;

public class OptionsEntryValueBoolean extends OptionsEntryValue<Boolean> {

    private final Button button;

    public OptionsEntryValueBoolean(String optionName, boolean value, Consumer<Boolean> save) {
        super(optionName, save);

        this.button = new Button(0, 0, 100, 20, new StringTextComponent(I18n.format("gui." + (value ? "yes" : "no"))), w -> {
            this.value = !this.value;
        });
        this.value = value;
    }

    @Override
    protected void drawValue(int entryWidth, int entryHeight, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        this.button.field_230690_l_ = x + 135;
        this.button.field_230691_m_ = y + entryHeight / 6;
        this.button.func_238482_a_(ITextComponent.func_241827_a_(I18n.format("gui." + (value ? "yes" : "no"))));
        this.button.func_230430_a_(new MatrixStack(), mouseX, mouseY, partialTicks);
    }

    @Override
    public IGuiEventListener getListener() {
        return button;
    }
}
