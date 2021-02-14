package mcp.mobius.waila;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import mcp.mobius.waila.gui.GuiConfigHome;

public class WailaConfigModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> new GuiConfigHome(null);
    }

}
