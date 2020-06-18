package mcp.mobius.waila.overlay;

import com.google.common.collect.Lists;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.ITaggableList;
import mcp.mobius.waila.api.RenderableTextComponent;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.TaggableList;
import mcp.mobius.waila.api.impl.TaggedTextComponent;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;

public class Tooltip {

    private final MinecraftClient client;
    private final List<Line> lines;
    private final boolean showItem;
    private final Dimension totalSize;

    public Tooltip(List<Text> Texts, boolean showItem) {
        WailaTooltipEvent event = new WailaTooltipEvent(Texts, DataAccessor.INSTANCE);
        WailaTooltipEvent.WAILA_HANDLE_TOOLTIP.invoker().onTooltip(event);

        this.client = MinecraftClient.getInstance();
        this.lines = Lists.newArrayList();
        this.showItem = showItem;
        this.totalSize = new Dimension();

        computeLines(Texts);
        addPadding();
    }

    public void computeLines(List<Text> Texts) {
        Texts.forEach(c -> {
            Dimension size = getLineSize(c, Texts);
            totalSize.setSize(Math.max(totalSize.width, size.width), totalSize.height + size.height);
            Text Text = c;
            if (Text instanceof TaggedTextComponent)
                Text = ((ITaggableList<Identifier, Text>) Texts).getTag(((TaggedTextComponent) Text).getTag());

            lines.add(new Line(Text, size));
        });
    }

    public void addPadding() {
        totalSize.width += hasItem() ? 30 : 10;
        totalSize.height += 8;
    }

    public void draw(MatrixStack matrices) {
        Rectangle position = getPosition();
        WailaConfig.ConfigOverlay.ConfigOverlayColor color = Waila.CONFIG.get().getOverlay().getColor();

        position.x += hasItem() ? 26 : 6;
        position.width += hasItem() ? 24 : 4;
        position.y += 6;

        for (Line line : lines) {
            if (line.getText() instanceof RenderableTextComponent) {
                RenderableTextComponent Text = (RenderableTextComponent) line.getText();
                int xOffset = 0;
                for (RenderableTextComponent.RenderContainer container : Text.getRenderers()) {
                    Dimension size = container.getRenderer().getSize(container.getData(), DataAccessor.INSTANCE);
                    container.getRenderer().draw(matrices, container.getData(), DataAccessor.INSTANCE, position.x + xOffset, position.y);
                    xOffset += size.width;
                }
            } else {
                client.textRenderer.drawWithShadow(matrices, line.getText(), position.x, position.y, color.getFontColor());
            }
            position.y += line.size.height;
        }
    }

    private Dimension getLineSize(Text text, List<Text> texts) {
        if (text instanceof RenderableTextComponent) {
            RenderableTextComponent renderable = (RenderableTextComponent) text;
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
        } else if (text instanceof TaggedTextComponent) {
            TaggedTextComponent tagged = (TaggedTextComponent) text;
            if (texts instanceof TaggableList) {
                Text taggedLine = ((TaggableList<Identifier, Text>) texts).getTag(tagged.getTag());
                return taggedLine == null ? new Dimension(0, 0) : getLineSize(taggedLine, texts);
            }
        }

        return new Dimension(client.textRenderer.getWidth(text.getString()), client.textRenderer.fontHeight + 1);
    }

    public List<Line> getLines() {
        return lines;
    }

    public boolean hasItem() {
        return showItem && Waila.CONFIG.get().getGeneral().shouldShowItem() && !RayTracing.INSTANCE.getIdentifierStack().isEmpty();
    }

    public Rectangle getPosition() {
        Window window = MinecraftClient.getInstance().getWindow();
        WailaConfig.ConfigOverlay.SizeChoice overlaySize = Waila.CONFIG.get().getOverlay().getOverlaySize();

        Rectangle position = new Rectangle(
                (int) ((window.getScaledWidth() * overlaySize.multiplier) * Waila.CONFIG.get().getOverlay().getOverlayPosX() - totalSize.width / 2), // Center it
                (int) ((window.getScaledHeight() * overlaySize.multiplier) * (1.0F - Waila.CONFIG.get().getOverlay().getOverlayPosY())),
                totalSize.width,
                totalSize.height
        );

        position.x *= overlaySize.multiplier;
        position.y *= overlaySize.multiplier;

        // Fix position to stay on screen
        if (position.x - position.width / 2 < 0)
            position.x = 0;

        if (position.x + position.width > MinecraftClient.getInstance().getWindow().getScaledWidth() * overlaySize.multiplier)
            position.x = (int) (MinecraftClient.getInstance().getWindow().getScaledWidth() * overlaySize.multiplier - position.width - 1);

        if (position.y + position.height > MinecraftClient.getInstance().getWindow().getScaledHeight())
            position.y = (int) (MinecraftClient.getInstance().getWindow().getScaledHeight() * overlaySize.multiplier - position.height - 1);

        return position;
    }

    public static class Line {

        private final Text Text;
        private final Dimension size;

        public Line(Text Text, Dimension size) {
            this.Text = Text;
            this.size = size;
        }

        public Text getText() {
            return Text;
        }

        public Dimension getSize() {
            return size;
        }
    }
}
