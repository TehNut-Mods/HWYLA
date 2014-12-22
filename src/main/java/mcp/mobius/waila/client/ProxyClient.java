package mcp.mobius.waila.client;

import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.gui.truetyper.FontLoader;
import mcp.mobius.waila.gui.truetyper.TrueTypeFont;
import mcp.mobius.waila.handlers.HUDHandlerBlocks;
import mcp.mobius.waila.handlers.HUDHandlerEntities;
import mcp.mobius.waila.server.ProxyServer;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import mcp.mobius.waila.cbcore.LangUtil;

public class ProxyClient extends ProxyServer {

	TrueTypeFont minecraftiaFont;
	
	//public static LangUtil lang = LangUtil.loadLangDir("waila");
	
	public ProxyClient() {}
	
	
	@Override
	public void registerHandlers(){
		
	    LangUtil.loadLangDir("waila");
		
		minecraftiaFont = FontLoader.createFont(new ResourceLocation("waila", "fonts/Minecraftia.ttf"), 14, true);

		//TODO : More NEI stuff
		//if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_NEWFILTERS, true))
		//	API.addSearchProvider(new ModNameFilter());
		
		//GuiContainerManager.addTooltipHandler(new TooltipHandlerWaila());
		
		// We mute the default keybind for displaying the tooltip
		//NEIClientConfig.getSetting(Constants.BIND_NEI_SHOW).setIntValue(Keyboard.KEY_NONE);
		//NEIClientConfig.getSetting(Constants.CFG_NEI_SHOW).setBooleanValue(false);
		
		//API.addKeyBind(Constants.BIND_WIKI, "Display wiki",          Keyboard.KEY_RSHIFT);
		//API.addKeyBind(Constants.BIND_TECH, "Display techtree",      Keyboard.KEY_RSHIFT);

		ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerBlocks(), Block.class);
		ModuleRegistrar.instance().registerTailProvider(new HUDHandlerBlocks(), Block.class);
		
		ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerEntities(), Entity.class);
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerEntities(), Entity.class);		
		ModuleRegistrar.instance().registerTailProvider(new HUDHandlerEntities(), Entity.class);
		
		//ModuleRegistrar.instance().registerShortDataProvider(new SummaryProviderDefault(), Item.class);
		
		ModuleRegistrar.instance().addConfig("General", "general.showhp");
		ModuleRegistrar.instance().addConfig("General", "general.showcrop");		
	}	
	
	@Override
	public Object getFont(){return this.minecraftiaFont;}	

}
