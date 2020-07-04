package mcp.mobius.waila.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.mobius.waila.gui.config.OptionsListWidget;
import mcp.mobius.waila.gui.config.value.OptionsEntryValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;

import java.util.Collection;
import java.util.List;

public abstract class GuiOptions extends Screen {

    private final Screen parent;
    private final Runnable saver;
    private final Runnable canceller;
    private OptionsListWidget options;

    public GuiOptions(Screen parent, TextComponent title, Runnable saver, Runnable canceller) {
        super(title);

        this.parent = parent;
        this.saver = saver;
        this.canceller = canceller;
    }

    public GuiOptions(Screen parent, TextComponent title) {
        this(parent, title, null, null);
    }

    @Override
    public void func_231158_b_(Minecraft client, int width, int height) {
        super.func_231158_b_(client, width, height);

        options = getOptions();
        field_230705_e_.add(options);
        func_231035_a_(options);

        if (saver != null && canceller != null) {
            func_230480_a_(new Button(width / 2 - 100, height - 25, 100, 20, new StringTextComponent(I18n.format("gui.done")), w -> {
                options.save();
                saver.run();
                func_231175_as__();
            }));
            func_230480_a_(new Button(width / 2 + 5, height - 25, 100, 20, new StringTextComponent(I18n.format("gui.cancel")), w -> {
                canceller.run();
                func_231175_as__();
            }));
        } else {
            func_230480_a_(new Button(width / 2 - 50, height - 25, 100, 20, new StringTextComponent(I18n.format("gui.done")), w -> {
                options.save();
                func_231175_as__();
            }));
        }
    }

    @Override
    public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        func_230446_a_(matrixStack);
        options.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
        func_238471_a_(matrixStack, field_230712_o_, field_230704_d_.getString(), field_230708_k_ / 2, 12, 16777215);
        super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);

        if (mouseY < 32 || mouseY > field_230709_l_ - 32)
            return;

        OptionsListWidget.Entry entry = options.func_230958_g_();
        if (entry instanceof OptionsEntryValue) {
            OptionsEntryValue value = (OptionsEntryValue) entry;

            if (I18n.hasKey(value.getDescription())) {
                int valueX = value.getX() + 10;
                String title = value.getTitle().getString();
                if (mouseX < valueX || mouseX > valueX + field_230712_o_.getStringWidth(title))
                    return;

                List<ITextProperties> tooltip = Lists.newArrayList(new StringTextComponent(title));
                tooltip.addAll(field_230712_o_.func_238425_b_(ITextProperties.func_240652_a_(I18n.format(value.getDescription())), 200));
                func_238654_b_(matrixStack, tooltip, mouseX, mouseY);
            }
        }
    }

    @Override
    public void func_231175_as__() {
        field_230706_i_.displayGuiScreen(parent);
    }

    public void addListener(IGuiEventListener listener) {
        field_230705_e_.add(listener);
    }

    public abstract OptionsListWidget getOptions();
}
