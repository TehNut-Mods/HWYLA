package mcp.mobius.waila.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiConfigHome extends Screen {

    private final Screen parent;

    public GuiConfigHome(Screen parent) {
        super(new TranslationTextComponent("gui.waila.configuration"));

        this.parent = parent;
    }

    @Override
    protected void func_231160_c_() {
        func_230480_a_(new Button(field_230708_k_ / 2 - 105, field_230709_l_ / 2 - 10, 100, 20, ITextComponent.func_241827_a_(I18n.format("gui.waila.waila_settings", Waila.NAME)), w -> {
            field_230706_i_.displayGuiScreen(new GuiConfigWaila(GuiConfigHome.this));
        }));
        func_230480_a_(new Button(field_230708_k_ / 2 + 5, field_230709_l_ / 2 - 10, 100, 20, ITextComponent.func_241827_a_("gui.waila.plugin_settings"), w -> {
            field_230706_i_.displayGuiScreen(new GuiConfigPlugins(GuiConfigHome.this));
        }));
        func_230480_a_(new Button(field_230708_k_ / 2 - 50, field_230709_l_ / 2 + 20, 100, 20, ITextComponent.func_241827_a_("gui.done"), w -> {
            Waila.CONFIG.save();
            PluginConfig.INSTANCE.save();
            field_230706_i_.displayGuiScreen(parent);
        }));
    }

    @Override
    public void func_230430_a_(MatrixStack matrixStack, int x, int y, float partialTicks) {
        func_230446_a_(matrixStack);
        func_238471_a_(matrixStack ,field_230712_o_, field_230704_d_.getString(), field_230708_k_ / 2, field_230709_l_ / 3, 16777215);
        super.func_230430_a_(matrixStack ,x, y, partialTicks);
    }
}
