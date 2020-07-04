package mcp.mobius.waila.gui.config.value;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;

import java.util.Locale;
import java.util.function.Consumer;

public class OptionsEntryValueEnum<T extends Enum<T>> extends OptionsEntryValue<T> {

    private final String translationKey;
    private final Button button;

    public OptionsEntryValueEnum(String optionName, T[] values, T selected, Consumer<T> save) {
        super(optionName, save);

        this.translationKey = optionName;
        this.button = new Button(0, 0, 100, 20, ITextComponent.func_241827_a_(I18n.format(optionName + "_" + selected.name().toLowerCase(Locale.ROOT))), w -> {
            value = values[(value.ordinal() + 1) % values.length];
        });
        this.value = selected;
    }

    @Override
    protected void drawValue(int entryWidth, int entryHeight, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        this.button.field_230690_l_ = x + 135;
        this.button.field_230691_m_ = y + entryHeight / 6;
        this.button.func_238482_a_(ITextComponent.func_241827_a_(I18n.format(translationKey + "_" + value.name().toLowerCase(Locale.ROOT))));
        this.button.func_230430_a_(new MatrixStack(), mouseX, mouseY, partialTicks);
    }

    @Override
    public IGuiEventListener getListener() {
        return button;
    }
}
