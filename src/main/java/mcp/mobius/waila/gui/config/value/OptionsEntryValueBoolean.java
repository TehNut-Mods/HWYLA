package mcp.mobius.waila.gui.config.value;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.TranslatableText;
import net.minecraft.client.util.math.MatrixStack;

import java.util.function.Consumer;

public class OptionsEntryValueBoolean extends OptionsEntryValue<Boolean> {

    private final ButtonWidget button;

    public OptionsEntryValueBoolean(String optionName, boolean value, Consumer<Boolean> save) {
        super(optionName, save);

        this.button = new ButtonWidget(0, 0, 100, 20, new TranslatableText("gui." + (value ? "yes" : "no")), w -> {
            this.value = !this.value;
        });
        this.value = value;
    }

    @Override
    protected void drawValue(MatrixStack matrices, int entryWidth, int entryHeight, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        this.button.x = x + 135;
        this.button.y = y + entryHeight / 6;
        this.button.setMessage(new TranslatableText("gui." + (value ? "yes" : "no")));
        this.button.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public Element getListener() {
        return button;
    }
}
