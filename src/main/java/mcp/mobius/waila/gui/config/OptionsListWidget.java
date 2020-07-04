package mcp.mobius.waila.gui.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mcp.mobius.waila.gui.GuiOptions;
import mcp.mobius.waila.gui.config.value.OptionsEntryValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;

public class OptionsListWidget extends AbstractList<OptionsListWidget.Entry> {

    private final GuiOptions owner;
    private final Runnable diskWriter;

    public OptionsListWidget(GuiOptions owner, Minecraft client, int x, int height, int width, int y, int entryHeight, Runnable diskWriter) {
        super(client, x, height, width, y, entryHeight);

        this.owner = owner;
        this.diskWriter = diskWriter;
    }

    public OptionsListWidget(GuiOptions owner, Minecraft client, int x, int height, int width, int y, int entryHeight) {
        this(owner, client, x, height, width, y, entryHeight, null);
    }

    @Override
    public int func_230949_c_() {
        return 250;
    }

    public void func_230430_a_(MatrixStack matrixStack, int int_1, int int_2, float float_1) {
        this.func_230433_a_(matrixStack);
        int int_3 = this.func_230952_d_();
        int int_4 = int_3 + 6;
        RenderSystem.disableLighting();
        RenderSystem.disableFog();
        Tessellator tessellator_1 = Tessellator.getInstance();
        BufferBuilder bufferBuilder_1 = tessellator_1.getBuffer();
        this.field_230668_b_.getTextureManager().bindTexture(field_230663_f_);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int int_5 = this.func_230968_n_();
        int int_6 = this.field_230672_i_ + 4 - (int)this.func_230966_l_();

        this.func_238478_a_(matrixStack, int_5, int_6, int_1, int_2, float_1);
        RenderSystem.disableDepthTest();
        this.func_238473_b_(matrixStack,0, this.field_230672_i_, 255, 255);
        this.func_238473_b_(matrixStack, this.field_230673_j_, this.field_230671_e_, 255, 255);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableTexture();
        bufferBuilder_1.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        bufferBuilder_1.pos(this.field_230675_l_, this.field_230672_i_ + 4, 0.0D).color(0, 0, 0, 0).tex(0.0f, 1.0f).endVertex();
        bufferBuilder_1.pos(this.field_230674_k_, this.field_230672_i_ + 4, 0.0D).color(0, 0, 0, 0).tex(1.0f, 1.0f).endVertex();
        bufferBuilder_1.pos(this.field_230674_k_, this.field_230672_i_, 0.0D).color(0, 0, 0, 255).tex(1.0f, 0.0f).endVertex();
        bufferBuilder_1.pos(this.field_230675_l_, this.field_230672_i_, 0.0D).color(0, 0, 0, 255).tex(0.0f, 0.0f).endVertex();
        bufferBuilder_1.pos(this.field_230675_l_, this.field_230673_j_, 0.0D).color(0, 0, 0, 255).tex(0.0f, 1.0f).endVertex();
        bufferBuilder_1.pos(this.field_230674_k_, this.field_230673_j_, 0.0D).color(0, 0, 0, 255).tex(1.0f, 1.0f).endVertex();
        bufferBuilder_1.pos(this.field_230674_k_, this.field_230673_j_ - 4, 0.0D).color(0, 0, 0, 0).tex(1.0f, 0.0f).endVertex();
        bufferBuilder_1.pos(this.field_230675_l_, this.field_230673_j_ - 4, 0.0D).color(0, 0, 0, 0).tex(0.0f, 0.0f).endVertex();
        tessellator_1.draw();
        int int_8 = Math.max(0, this.func_230945_b_() - (this.field_230673_j_ - this.field_230672_i_ - 4));
        if (int_8 > 0) {
            int int_9 = (int)((float)((this.field_230673_j_ - this.field_230672_i_) * (this.field_230673_j_ - this.field_230672_i_)) / (float)this.func_230945_b_());
            int_9 = MathHelper.clamp(int_9, 32, this.field_230673_j_ - this.field_230672_i_ - 8);
            int int_10 = (int)this.func_230966_l_() * (this.field_230673_j_ - this.field_230672_i_ - int_9) / int_8 + this.field_230672_i_;
            if (int_10 < this.field_230672_i_) {
                int_10 = this.field_230672_i_;
            }

            bufferBuilder_1.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
            bufferBuilder_1.pos(int_3, this.field_230673_j_, 0.0D).color(0, 0, 0, 255).tex(0.0f, 1.0f).endVertex();
            bufferBuilder_1.pos(int_4, this.field_230673_j_, 0.0D).color(0, 0, 0, 255).tex(1.0f, 1.0f).endVertex();
            bufferBuilder_1.pos(int_4, this.field_230672_i_, 0.0D).color(0, 0, 0, 255).tex(1.0f, 0.0f).endVertex();
            bufferBuilder_1.pos(int_3, this.field_230672_i_, 0.0D).color(0, 0, 0, 255).tex(0.0f, 0.0f).endVertex();
            bufferBuilder_1.pos(int_3, (int_10 + int_9), 0.0D).color(128, 128, 128, 255).tex(0.0f, 1.0f).endVertex();
            bufferBuilder_1.pos(int_4, (int_10 + int_9), 0.0D).color(128, 128, 128, 255).tex(1.0f, 1.0f).endVertex();
            bufferBuilder_1.pos(int_4, int_10, 0.0D).color(128, 128, 128, 255).tex(1.0f, 0.0f).endVertex();
            bufferBuilder_1.pos(int_3, int_10, 0.0D).color(128, 128, 128, 255).tex(0.0f, 0.0f).endVertex();
            bufferBuilder_1.pos(int_3, (int_10 + int_9 - 1), 0.0D).color(192, 192, 192, 255).tex(0.0f, 1.0f).endVertex();
            bufferBuilder_1.pos(int_4 - 1, int_10 + int_9 - 1, 0.0D).color(192, 192, 192, 255).tex(1.0f, 1.0f).endVertex();
            bufferBuilder_1.pos(int_4 - 1, int_10, 0.0D).color(192, 192, 192, 255).tex(1.0f, 0.0f).endVertex();
            bufferBuilder_1.pos(int_3, int_10, 0.0D).color(192, 192, 192, 255).tex(0.0f, 0.0f).endVertex();
            tessellator_1.draw();
        }

        this.func_230447_a_(matrixStack ,int_1, int_2);
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }

    public void save() {
        func_231039_at__()
                .stream()
                .filter(e -> e instanceof OptionsEntryValue)
                .map(e -> (OptionsEntryValue) e)
                .forEach(OptionsEntryValue::save);
        if (diskWriter != null)
            diskWriter.run();
    }

    public void add(Entry entry) {
        if (entry instanceof OptionsEntryValue) {
            IGuiEventListener element = ((OptionsEntryValue) entry).getListener();
            if (element != null)
                owner.addListener(element);
        }
        func_230513_b_(entry);
    }

    public abstract static class Entry extends AbstractList.AbstractListEntry<Entry> {

        protected final Minecraft client;

        public Entry() {
            this.client = Minecraft.getInstance();
        }

        @Override
        public abstract void func_230432_a_(MatrixStack matrixStack, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime);

    }
}
