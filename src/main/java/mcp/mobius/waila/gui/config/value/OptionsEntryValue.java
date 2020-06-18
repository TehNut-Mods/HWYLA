package mcp.mobius.waila.gui.config.value;

import mcp.mobius.waila.gui.config.OptionsListWidget;
import net.minecraft.client.gui.Element;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.client.util.math.MatrixStack;

import java.util.function.Consumer;

public abstract class OptionsEntryValue<T> extends OptionsListWidget.Entry {

    private final Text title;
    private final String description;
    protected final Consumer<T> save;
    protected T value;
    private int x;

    public OptionsEntryValue(String optionName, Consumer<T> save) {
        this.title = new TranslatableText(optionName);
        this.description = optionName + "_desc";
        this.save = save;
    }

    @Override
    public final void render(MatrixStack matrices, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
        client.textRenderer.drawWithShadow(matrices, title.getString(), rowLeft + 10, rowTop + (height / 4) + (client.textRenderer.fontHeight / 2), 16777215);
        drawValue(matrices, width, height, rowLeft, rowTop, mouseX, mouseY, hovered, deltaTime);
        this.x = rowLeft;
    }

    public void save() {
        save.accept(value);
    }

    public Element getListener() {
        return null;
    }

    public Text getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getX() {
        return x;
    }

    protected abstract void drawValue(MatrixStack matrices, int entryWidth, int entryHeight, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks);
}
