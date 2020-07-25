package mcp.mobius.waila.overlay;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.ITaggableList;
import mcp.mobius.waila.api.RenderableTextComponent;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.TaggableList;
import mcp.mobius.waila.api.impl.TaggedTextComponent;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;

public class Tooltip {

    private final Minecraft client;
    private final List<Line> lines;
    private final boolean showItem;
    private final Dimension totalSize;

    public Tooltip(List<ITextComponent> components, boolean showItem) {
        WailaTooltipEvent event = new WailaTooltipEvent(components, DataAccessor.INSTANCE);
        MinecraftForge.EVENT_BUS.post(event);

        this.client = Minecraft.getInstance();
        this.lines = Lists.newArrayList();
        this.showItem = showItem;
        this.totalSize = new Dimension();

        computeLines(components);
        addPadding();
    }

    public void computeLines(List<ITextComponent> components) {
        components.forEach(c -> {
            Dimension size = getLineSize(c, components);
            totalSize.setSize(Math.max(totalSize.width, size.width), totalSize.height + size.height);
            ITextComponent component = c;
            if (component instanceof TaggedTextComponent)
                component = ((ITaggableList<ResourceLocation, ITextComponent>) components).getTag(((TaggedTextComponent) component).getTag());

            lines.add(new Line(component, size));
        });
    }

    public void addPadding() {
        totalSize.width += hasItem() ? 30 : 10;
        totalSize.height += 8;
    }

    public void draw() {
        Rectangle position = getPosition();
        WailaConfig.ConfigOverlay.ConfigOverlayColor color = Waila.CONFIG.get().getOverlay().getColor();

        position.x += hasItem() ? 26 : 6;
        position.width += hasItem() ? 24 : 4;
        position.y += 6;

        MatrixStack matrixStack = new MatrixStack();

        for (Line line : lines) {
            if (line.getComponent() instanceof RenderableTextComponent) {
                RenderableTextComponent component = (RenderableTextComponent) line.getComponent();
                int xOffset = 0;
                for (RenderableTextComponent.RenderContainer container : component.getRenderers()) {
                    Dimension size = container.getRenderer().getSize(container.getData(), DataAccessor.INSTANCE);
                    container.getRenderer().draw(container.getData(), DataAccessor.INSTANCE, position.x + xOffset, position.y);
                    xOffset += size.width;
                }
            } else {
                client.fontRenderer.drawStringWithShadow(matrixStack, line.getComponent().getString(), position.x, position.y, color.getFontColor());
            }
            position.y += line.size.height;
        }
    }

    private Dimension getLineSize(ITextComponent component, List<ITextComponent> components) {
        if (component instanceof RenderableTextComponent) {
            RenderableTextComponent renderable = (RenderableTextComponent) component;
            List<RenderableTextComponent.RenderContainer> renderers = renderable.getRenderers();
            if (renderers.isEmpty())
                return new Dimension(0, 0);

            int width = 0;
            int height = 0;
            for (RenderableTextComponent.RenderContainer container : renderers) {
                Dimension iconSize = container.getRenderer().getSize(container.getData(), DataAccessor.INSTANCE);
                width += iconSize.width;
                height = Math.max(height, iconSize.height);
            }

            return new Dimension(width, height);
        } else if (component instanceof TaggedTextComponent) {
            TaggedTextComponent tagged = (TaggedTextComponent) component;
            if (components instanceof TaggableList) {
                ITextComponent taggedLine = ((TaggableList< ResourceLocation, ITextComponent>) components).getTag(tagged.getTag());
                return taggedLine == null ? new Dimension(0, 0) : getLineSize(taggedLine, components);
            }
        }

        return new Dimension(client.fontRenderer.getStringWidth(component.getString()), client.fontRenderer.FONT_HEIGHT + 1);
    }

    public List<Line> getLines() {
        return lines;
    }

    public boolean hasItem() {
        return showItem && Waila.CONFIG.get().getGeneral().shouldShowItem() && !RayTracing.INSTANCE.getIdentifierStack().isEmpty();
    }

    public Rectangle getPosition() {
        MainWindow window = Minecraft.getInstance().getMainWindow();
        return new Rectangle(
                (int) (window.getScaledWidth() * Waila.CONFIG.get().getOverlay().getOverlayPosX() - totalSize.width / 2), // Center it
                (int) (window.getScaledHeight() * (1.0F - Waila.CONFIG.get().getOverlay().getOverlayPosY())),
                totalSize.width,
                totalSize.height
        );
    }

    public static class Line {

        private final ITextComponent component;
        private final Dimension size;

        public Line(ITextComponent component, Dimension size) {
            this.component = component;
            this.size = size;
        }

        public ITextComponent getComponent() {
            return component;
        }

        public Dimension getSize() {
            return size;
        }
    }
}
