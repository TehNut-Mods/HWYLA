package mcp.mobius.waila.gui.screens.config;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.interfaces.*;
import mcp.mobius.waila.gui.screens.ScreenBase;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.LayoutBase;
import mcp.mobius.waila.gui.widgets.LayoutMargin;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import mcp.mobius.waila.gui.widgets.buttons.ButtonLabel;
import mcp.mobius.waila.gui.widgets.buttons.ButtonScreenChange;
import mcp.mobius.waila.gui.widgets.movable.PictureMovableRC;
import mcp.mobius.waila.config.OverlayConfig;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.Configuration;

public class ScreenHUDConfig extends ScreenBase {

    public ScreenHUDConfig(GuiScreen parent) {
        super(parent);

        this.getRoot().addWidget("EventCanvas", new EventCanvas(this.getRoot(), parent));
    }

    private class EventCanvas extends LayoutBase {
        IWidget draggedWidget = null;

        float scale;

        public EventCanvas(IWidget parent, GuiScreen prevScreen) {
            super(parent);
            this.setGeometry(new WidgetGeometry(0.0, 0.0, 100.0, 100.0, CType.RELXY, CType.RELXY));

			/*
			IWidget layoutX = this.addWidget("LayoutX", new LayoutBase(this));
			layoutX.setGeometry(new WidgetGeometry(40.0,50.0,20,60, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
			layoutX.addWidget("ButtonXAdd", new ButtonLabel(null, "+")).setGeometry(new WidgetGeometry(0.0,  0.0,20,20, CType.RELXY, CType.ABSXY, WAlign.LEFT, WAlign.TOP));
			layoutX.addWidget("ButtonXSub", new ButtonLabel(null, "-")).setGeometry(new WidgetGeometry(0.0,100.0,20,20, CType.RELXY, CType.ABSXY, WAlign.LEFT, WAlign.BOTTOM));
			layoutX.addWidget("ValueDisplayX",   new LabelFixedFont(null, "0")).setGeometry(new WidgetGeometry(50.0,50.0,20,20, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
			*/

			/*
			IWidget layoutY = this.addWidget("LayoutY", new LayoutBase(this));
			layoutY.setGeometry(new WidgetGeometry(50.0,50.0,20,60,CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
			layoutY.addWidget("ButtonYAdd", new ButtonLabel(null, "+")).setGeometry(new WidgetGeometry(0.0,  0.0,20,20, CType.RELXY, CType.ABSXY, WAlign.LEFT, WAlign.TOP));
			layoutY.addWidget("ButtonYSub", new ButtonLabel(null, "-")).setGeometry(new WidgetGeometry(0.0,100.0,20,20, CType.RELXY, CType.ABSXY, WAlign.LEFT, WAlign.BOTTOM));
			layoutY.addWidget("ValueDisplayY",   new LabelFixedFont(null, "0")).setGeometry(new WidgetGeometry(50.0,50.0,20,20, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
			*/

            double picX = ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSX, 0) / 100.0;
            double picY = ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSY, 0) / 100.0;
            float picAlpha = ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_ALPHA, 0) / 100.0f;
            scale = ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SCALE, 0) / 100.0f;

            int picSX = (int) (180 / this.rez.getScaleFactor() * scale), picSY = (int) (62 / this.rez.getScaleFactor() * scale);
            this.addWidget("Layout", new LayoutMargin(null), RenderPriority.HIGH);
            ((LayoutMargin) this.getWidget("Layout")).setMargins(picSX / 2, picSX / 2, picSY / 2, picSY / 2);
            this.getWidget("Layout").addWidget("Picture", new PictureMovableRC(null, "waila:textures/config_template_bg.png")).setGeometry(new WidgetGeometry(picX, picY, picSX, picSY, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
            this.getWidget("Layout").getWidget("Picture").addWidget("Foreground", new PictureMovableRC(null, "waila:textures/config_template_fg.png")).setGeometry(new WidgetGeometry(picX, picY, picSX, picSY, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
            IWidget foreground = getWidget("Layout").getWidget("Picture").getWidget("Foreground");
            foreground.setPos(picX, picY + 45);

            this.addWidget("TextTuto1", new LabelFixedFont(null, "screen.label.dragtuto")).setGeometry(new WidgetGeometry(50.0, 30.0, 20, 20, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));

            IWidget layoutX = this.addWidget("LayoutX", new LayoutBase(this));
            layoutX.setGeometry(new WidgetGeometry(35.0, 50.0, 20, 80, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
            layoutX.addWidget("LabelX", new LabelFixedFont(null, "X")).setGeometry(new WidgetGeometry(50.0, 0.0, 20, 20, CType.REL_X, CType.ABSXY, WAlign.CENTER, WAlign.TOP));
            layoutX.addWidget("ButtonXAdd", new ButtonLabel(null, "+")).setGeometry(new WidgetGeometry(0.0, 10.0, 20, 20, CType.REL_X, CType.ABSXY, WAlign.LEFT, WAlign.TOP));
            layoutX.addWidget("ValueDisplayX", new LabelFixedFont(null, "0")).setGeometry(new WidgetGeometry(50.0, 40.0, 20, 20, CType.REL_X, CType.ABSXY, WAlign.CENTER, WAlign.TOP));
            layoutX.addWidget("ButtonXSub", new ButtonLabel(null, "-")).setGeometry(new WidgetGeometry(0.0, 60.0, 20, 20, CType.REL_X, CType.ABSXY, WAlign.LEFT, WAlign.TOP));

            IWidget layoutY = this.addWidget("LayoutY", new LayoutBase(this));
            layoutY.setGeometry(new WidgetGeometry(45.0, 50.0, 20, 80, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
            layoutY.addWidget("LabelY", new LabelFixedFont(null, "Y")).setGeometry(new WidgetGeometry(50.0, 0.0, 20, 20, CType.REL_X, CType.ABSXY, WAlign.CENTER, WAlign.TOP));
            layoutY.addWidget("ButtonYAdd", new ButtonLabel(null, "+")).setGeometry(new WidgetGeometry(0.0, 10.0, 20, 20, CType.REL_X, CType.ABSXY, WAlign.LEFT, WAlign.TOP));
            layoutY.addWidget("ValueDisplayY", new LabelFixedFont(null, "0")).setGeometry(new WidgetGeometry(50.0, 40.0, 20, 20, CType.REL_X, CType.ABSXY, WAlign.CENTER, WAlign.TOP));
            layoutY.addWidget("ButtonYSub", new ButtonLabel(null, "-")).setGeometry(new WidgetGeometry(0.0, 60.0, 20, 20, CType.REL_X, CType.ABSXY, WAlign.LEFT, WAlign.TOP));

            IWidget layoutAlpha = this.addWidget("LayoutAlpha", new LayoutBase(this));
            layoutAlpha.setGeometry(new WidgetGeometry(55.0, 50.0, 20, 80, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
            layoutAlpha.addWidget("LabelAlpha", new LabelFixedFont(null, "Alpha")).setGeometry(new WidgetGeometry(50.0, 0.0, 20, 20, CType.REL_X, CType.ABSXY, WAlign.CENTER, WAlign.TOP));
            layoutAlpha.addWidget("ButtonAlphaAdd", new ButtonLabel(null, "+")).setGeometry(new WidgetGeometry(0.0, 10.0, 20, 20, CType.REL_X, CType.ABSXY, WAlign.LEFT, WAlign.TOP));
            layoutAlpha.addWidget("ValueDisplayAlpha", new LabelFixedFont(null, "0")).setGeometry(new WidgetGeometry(50.0, 40.0, 20, 20, CType.REL_X, CType.ABSXY, WAlign.CENTER, WAlign.TOP));
            layoutAlpha.addWidget("ButtonAlphaSub", new ButtonLabel(null, "-")).setGeometry(new WidgetGeometry(0.0, 60.0, 20, 20, CType.REL_X, CType.ABSXY, WAlign.LEFT, WAlign.TOP));

            IWidget layoutScale = this.addWidget("LayoutScale", new LayoutBase(this));
            layoutScale.setGeometry(new WidgetGeometry(65.0, 50.0, 20, 80, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
            layoutScale.addWidget("LabelScale", new LabelFixedFont(null, "screen.label.scale")).setGeometry(new WidgetGeometry(50.0, 0.0, 20, 20, CType.REL_X, CType.ABSXY, WAlign.CENTER, WAlign.TOP));
            layoutScale.addWidget("ButtonScaleAdd", new ButtonLabel(null, "+")).setGeometry(new WidgetGeometry(0.0, 10.0, 20, 20, CType.REL_X, CType.ABSXY, WAlign.LEFT, WAlign.TOP));
            layoutScale.addWidget("ValueDisplayScale", new LabelFixedFont(null, "0")).setGeometry(new WidgetGeometry(50.0, 40.0, 20, 20, CType.REL_X, CType.ABSXY, WAlign.CENTER, WAlign.TOP));
            layoutScale.addWidget("ButtonScaleSub", new ButtonLabel(null, "-")).setGeometry(new WidgetGeometry(0.0, 60.0, 20, 20, CType.REL_X, CType.ABSXY, WAlign.LEFT, WAlign.TOP));


            this.addWidget("ButtonCancel", new ButtonScreenChange(null, "screen.button.cancel", prevScreen)).setGeometry(new WidgetGeometry(70.0, 97.0, 75, 20, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.BOTTOM));
            this.addWidget("ButtonOk", new ButtonScreenChange(null, "screen.button.ok", prevScreen)).setGeometry(new WidgetGeometry(30.0, 97.0, 75, 20, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.BOTTOM));
            this.addWidget("ButtonDefault", new ButtonLabel(null, "screen.button.default")).setGeometry(new WidgetGeometry(50.0, 97.0, 75, 20, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.BOTTOM));


            ((LabelFixedFont) this.getWidget("LayoutX").getWidget("ValueDisplayX")).setText(String.format("%.2f", picX));
            ((LabelFixedFont) this.getWidget("LayoutY").getWidget("ValueDisplayY")).setText(String.format("%.2f", picY));
            ((LabelFixedFont) this.getWidget("LayoutAlpha").getWidget("ValueDisplayAlpha")).setText(String.format("%.2f", picAlpha));
            ((LabelFixedFont) this.getWidget("LayoutScale").getWidget("ValueDisplayScale")).setText(String.format("%.2f", scale));

            this.getWidget("Layout").getWidget("Picture").setAlpha(picAlpha);

        }

        @Override
        public IWidget getWidgetAtCoordinates(double posX, double posY) {
            if (this.getWidget("Layout").getWidget("Picture").isWidgetAtCoordinates(posX, posY))
                return this.getWidget("Layout").getWidget("Picture");
            else
                return super.getWidgetAtCoordinates(posX, posY);
        }

        @Override
        public void onMouseClick(MouseEvent event) {
            //System.out.println(this.getWidgetAtCoordinates(event.x, event.y));

            if ((event.button == 0) && (this.getWidget("Layout").getWidget("Picture").isWidgetAtCoordinates(event.x, event.y))) {
                this.getWidget("Layout").getWidget("Picture").onMouseClick(event);
                this.draggedWidget = this.getWidget("Layout").getWidget("Picture");
            } else
                super.onMouseClick(event);
        }

        @Override
        public void onMouseDrag(MouseEvent event) {

            //if ((this.getWidgetAtCoordinates(event.x, event.y) == this) && (this.draggedWidget != null)){
            if (this.draggedWidget != null) {
                this.draggedWidget.onMouseDrag(event);
            } else {
                super.onMouseDrag(event);
            }
        }

        @Override
        public void onMouseRelease(MouseEvent event) {
            if (event.button == 0)
                this.draggedWidget = null;
            super.onMouseRelease(event);
        }


        private void updateData() {
            ((LabelFixedFont) this.getWidget("LayoutX").getWidget("ValueDisplayX")).setText(String.format("%.2f", this.getWidget("Layout").getWidget("Picture").getGeometry().getRawPos().getX()));
            ((LabelFixedFont) this.getWidget("LayoutY").getWidget("ValueDisplayY")).setText(String.format("%.2f", this.getWidget("Layout").getWidget("Picture").getGeometry().getRawPos().getY()));
            ((LabelFixedFont) this.getWidget("LayoutAlpha").getWidget("ValueDisplayAlpha")).setText(String.format("%.2f", this.getWidget("Layout").getWidget("Picture").getAlpha()));
            ((LabelFixedFont) this.getWidget("LayoutScale").getWidget("ValueDisplayScale")).setText(String.format("%.2f", scale));
        }

        @Override
        public void onWidgetEvent(IWidget srcwidget, Signal signal, Object... params) {

            if (this.getWidget("Layout") == null) return;

            if (srcwidget.equals(this.getWidget("Layout").getWidget("Picture")) && signal == Signal.DRAGGED) {
                this.draggedWidget = this.getWidget("Layout").getWidget("Picture");
                this.updateData();
            }

            if (signal == Signal.CLICKED) {

                IWidget picture = this.getWidget("Layout").getWidget("Picture");
                double pictureX = picture.getGeometry().getRawPos().getX();
                double pictureY = picture.getGeometry().getRawPos().getY();
                double pixelToPercentX = 100.0D / picture.getParent().getSize().getX();
                double pixelToPercentY = 100.0D / picture.getParent().getSize().getY();

                if (srcwidget.equals(this.getWidget("LayoutX").getWidget("ButtonXAdd"))) {
                    double newPos = Math.min(pictureX + pixelToPercentX, 100.0);
                    picture.setPos(newPos, pictureY);
                }

                if (srcwidget.equals(this.getWidget("LayoutX").getWidget("ButtonXSub"))) {
                    double newPos = Math.max(pictureX - pixelToPercentX, 0.0);
                    picture.setPos(newPos, pictureY);
                }

                if (srcwidget.equals(this.getWidget("LayoutY").getWidget("ButtonYAdd"))) {
                    double newPos = Math.min(pictureY + pixelToPercentY, 100.0);
                    picture.setPos(pictureX, newPos);
                }

                if (srcwidget.equals(this.getWidget("LayoutY").getWidget("ButtonYSub"))) {
                    double newPos = Math.max(pictureY - pixelToPercentY, 0.0);
                    picture.setPos(pictureX, newPos);
                }

                if (srcwidget.equals(this.getWidget("LayoutAlpha").getWidget("ButtonAlphaAdd")))
                    picture.setAlpha(Math.min(picture.getAlpha() + 0.05f, 1.0f));

                if (srcwidget.equals(this.getWidget("LayoutAlpha").getWidget("ButtonAlphaSub")))
                    picture.setAlpha(Math.max(picture.getAlpha() - 0.05f, 0.0f));

                if (srcwidget.equals(this.getWidget("LayoutScale").getWidget("ButtonScaleAdd"))) {
                    scale += 0.05f;
                    scale = Math.max(0.10f, scale);
                    scale = Math.min(2.00f, scale);

                    int picSX = (int) (180 / this.rez.getScaleFactor() * scale), picSY = (int) (62 / this.rez.getScaleFactor() * scale);
                    picture.setSize(picSX, picSY);
                    picture.getWidget("Foreground").setSize(picSX, picSY);
                    ((LayoutMargin) this.getWidget("Layout")).setMargins(picSX / 2, picSX / 2, picSY / 2, picSY / 2);
                }

                if (srcwidget.equals(this.getWidget("LayoutScale").getWidget("ButtonScaleSub"))) {
                    scale -= 0.05f;
                    scale = Math.max(0.10f, scale);
                    scale = Math.min(2.00f, scale);

                    int picSX = (int) (180 / this.rez.getScaleFactor() * scale), picSY = (int) (62 / this.rez.getScaleFactor() * scale);
                    picture.setSize(picSX, picSY);
                    picture.getWidget("Foreground").setSize(picSX, picSY);
                    ((LayoutMargin) this.getWidget("Layout")).setMargins(picSX / 2, picSX / 2, picSY / 2, picSY / 2);
                }

                //picture.emit(Signal.DRAGGED, picture.getPos());
                this.updateData();

            }
            if (srcwidget.equals(this.getWidget("ButtonOk")) && signal == Signal.CLICKED) {
                ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSX, (int) (this.getWidget("Layout").getWidget("Picture").getGeometry().getRawPos().getX() * 100.0));
                ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSY, (int) (this.getWidget("Layout").getWidget("Picture").getGeometry().getRawPos().getY() * 100.0));
                ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_ALPHA, (int) (this.getWidget("Layout").getWidget("Picture").getAlpha() * 100.0));
                ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SCALE, (int) (scale * 100.0));

                OverlayConfig.alpha = ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_ALPHA, 0);
                OverlayConfig.posX = ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSX, 0);
                OverlayConfig.posY = ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSY, 0);
                OverlayConfig.scale = ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SCALE, 0) / 100.0f;

                OverlayConfig.updateColors();
            }

            if (srcwidget.equals(this.getWidget("ButtonDefault")) && signal == Signal.CLICKED) {
                this.getWidget("Layout").getWidget("Picture").setPos(50.0, 1.0);
                this.getWidget("Layout").getWidget("Picture").setAlpha(0.8f);
                this.scale = 1.0f;
                int picSX = (int) (180 / this.rez.getScaleFactor() * scale), picSY = (int) (62 / this.rez.getScaleFactor() * scale);
                this.getWidget("Layout").getWidget("Picture").setSize(picSX, picSY);
                this.getWidget("Layout").getWidget("Picture").getWidget("Foreground").setSize(picSX, picSY);
                ((LayoutMargin) this.getWidget("Layout")).setMargins(picSX / 2, picSX / 2, picSY / 2, picSY / 2);

                this.updateData();
            }
        }
    }

}
