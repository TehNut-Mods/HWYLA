package mcp.mobius.waila.gui.config.value;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class OptionsEntryValueCycle extends OptionsEntryValue<String> {

    private final String translationKey;
    private final Button button;
    private final boolean createLocale;

    public OptionsEntryValueCycle(String optionName, String[] values, String selected, Consumer<String> save, boolean createLocale) {
        super(optionName, save);

        this.translationKey = optionName;
        this.createLocale = createLocale;
        List<String> vals = Arrays.asList(values);
        this.button = new Button(0, 0, 100, 20, ITextComponent.func_241827_a_(createLocale ? I18n.format(optionName + "_" + selected.replace(" ", "_").toLowerCase(Locale.ROOT)) : selected ), w -> {
            value = vals.get((vals.indexOf(value) + 1) % vals.size());
        });
        this.value = selected;
    }

    public OptionsEntryValueCycle(String optionName, String[] values, String selected, Consumer<String> save) {
        this(optionName, values, selected, save, false);
    }

    @Override
    protected void drawValue(int entryWidth, int entryHeight, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        this.button.field_230690_l_ = x + 135;
        this.button.field_230691_m_ = y + entryHeight / 6;
        this.button.func_238482_a_(ITextComponent.func_241827_a_(createLocale ? I18n.format(translationKey + "_" + value.replace(" ", "_").toLowerCase(Locale.ROOT)) : value));
        this.button.func_230430_a_(new MatrixStack(), mouseX, mouseY, partialTicks);
    }

    @Override
    public IGuiEventListener getListener() {
        return button;
    }
}
