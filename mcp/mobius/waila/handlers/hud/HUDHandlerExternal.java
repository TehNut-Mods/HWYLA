package mcp.mobius.waila.handlers.hud;

import java.util.ArrayList;
import java.util.List;

import mcp.mobius.waila.WailaExceptionHandler;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ConfigHandler;
import mcp.mobius.waila.addons.ExternalModulesHandler;
import mcp.mobius.waila.api.IWailaBlock;
import mcp.mobius.waila.api.IWailaBlockDecorator;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.handlers.DataAccessor;
import mcp.mobius.waila.network.Packet0x01TERequest;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import codechicken.nei.api.IHighlightHandler;
import codechicken.nei.api.ItemInfo.Layout;
import cpw.mods.fml.common.network.PacketDispatcher;

public class HUDHandlerExternal implements IHighlightHandler {
	
	private ArrayList<IWailaDataProvider>   headProviders = new ArrayList<IWailaDataProvider>();
	private ArrayList<IWailaDataProvider>   bodyProviders = new ArrayList<IWailaDataProvider>();
	private ArrayList<IWailaDataProvider>   tailProviders = new ArrayList<IWailaDataProvider>();
	private MovingObjectPosition prevMOP = null;
	private int prevWorldID = -999;
	private DataAccessor accessor = null;
	
	private static HUDHandlerExternal _instance;
	private HUDHandlerExternal(){}
	public static HUDHandlerExternal instance(){
		if(_instance == null)
			_instance = new HUDHandlerExternal();			
		return _instance;
	}	
	
	@Override
	public ItemStack identifyHighlight(World world, EntityPlayer player, MovingObjectPosition mop) {
		if (this.prevMOP == null)
			this.prevMOP = mop;
		
		if (this.areMopDifferent(player, mop) || accessor == null){
			accessor = DataAccessor.instance;
			accessor.set(world, player, mop);
		}
		
		Block block   = accessor.getBlock();
		int   blockID = accessor.getBlockID();
		
		if (IWailaBlock.class.isInstance(block)){
			try{
				return ((IWailaBlock)block).getWailaStack(accessor, ConfigHandler.instance());
			}catch (Throwable e){
				WailaExceptionHandler.handleErr(e, block.getClass().toString(), null);
			}
		}

		if(ExternalModulesHandler.instance().hasStackProviders(blockID)){
			for (IWailaDataProvider dataProvider : ExternalModulesHandler.instance().getStackProviders(blockID)){
				try{
					ItemStack retval = dataProvider.getWailaStack(accessor, ConfigHandler.instance());
					if (retval != null)
						return retval;					
				}catch (Throwable e){
					WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), null);					
				}				
			}
		}
		return null;
	}

	@Override
	public List<String> handleTextData(ItemStack itemStack, World world, EntityPlayer player, MovingObjectPosition mop, List<String> currenttip, Layout layout) {
		if (this.prevMOP == null)
			this.prevMOP = mop;
		
		if (this.areMopDifferent(player, mop) || accessor == null){
			accessor = DataAccessor.instance;
			accessor.set(world, player, mop);
		}
		
		Block block   = accessor.getBlock();
		int   blockID = accessor.getBlockID();
		
		if (accessor.getTileEntity() != null && mod_Waila.instance.serverPresent && 
				((System.currentTimeMillis() - accessor.timeLastUpdate >= 250) || this.areMopDifferent(player, mop))){
			accessor.timeLastUpdate = System.currentTimeMillis();
			PacketDispatcher.sendPacketToServer(Packet0x01TERequest.create(world, mop));
		}

		/* Interface IWailaBlock */
		if (IWailaBlock.class.isInstance(block)){
			TileEntity entity = world.getBlockTileEntity(mop.blockX, mop.blockY, mop.blockZ);
			if (layout == Layout.HEADER)
				try{				
					return ((IWailaBlock)block).getWailaHead(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					return WailaExceptionHandler.handleErr(e, block.getClass().toString(), currenttip);
				}					
			else if (layout == Layout.BODY)
				try{					
					return ((IWailaBlock)block).getWailaBody(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					return WailaExceptionHandler.handleErr(e, block.getClass().toString(), currenttip);
				}
			else if (layout == Layout.FOOTER)
				try{					
					return ((IWailaBlock)block).getWailaTail(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					return WailaExceptionHandler.handleErr(e, block.getClass().toString(), currenttip);
				}				
		}		
		
		if (this.areMopDifferent(player, mop)){

			headProviders.clear();
			bodyProviders.clear();
			tailProviders.clear();
			
			this.prevMOP     = mop;
			this.prevWorldID = player.dimension;
			
			//if (layout == Layout.HEADER && !ExternalModulesHandler.instance().hasCachedHeadProviders(block))
				ExternalModulesHandler.instance().cacheHeadProvider(block);

			//else if (layout == Layout.BODY   && !ExternalModulesHandler.instance().hasCachedBodyProviders(block))
				ExternalModulesHandler.instance().cacheBodyProvider(block);
			
			//else if (layout == Layout.FOOTER && !ExternalModulesHandler.instance().hasCachedTailProviders(block))
				ExternalModulesHandler.instance().cacheTailProvider(block);			

			if (accessor.getTileEntity() != null){
			//if (layout == Layout.HEADER && !ExternalModulesHandler.instance().hasCachedHeadProviders(accessor.getTileEntity()))
				ExternalModulesHandler.instance().cacheHeadProvider(accessor.getTileEntity());
				ExternalModulesHandler.instance().cacheBodyProvider(accessor.getTileEntity());
				ExternalModulesHandler.instance().cacheTailProvider(accessor.getTileEntity());	
			}
			
			if (layout == Layout.HEADER){
				headProviders.addAll(ExternalModulesHandler.instance().getCachedHeadProviders(block));
				headProviders.addAll(ExternalModulesHandler.instance().getCachedHeadProviders(accessor.getTileEntity()));
			}
			else if (layout == Layout.BODY){
				bodyProviders.addAll(ExternalModulesHandler.instance().getCachedBodyProviders(block));
				bodyProviders.addAll(ExternalModulesHandler.instance().getCachedBodyProviders(accessor.getTileEntity()));
			}
			else if (layout == Layout.FOOTER){
				tailProviders.addAll(ExternalModulesHandler.instance().getCachedTailProviders(block));
				tailProviders.addAll(ExternalModulesHandler.instance().getCachedTailProviders(accessor.getTileEntity()));
			}
		}
		
		/* Apply all collected providers */
		if (layout == Layout.HEADER){
			for (IWailaDataProvider dataProvider : headProviders){
				try{				
					currenttip = dataProvider.getWailaHead(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
				}
			}
		}
		else if (layout == Layout.BODY){
			for (IWailaDataProvider dataProvider : bodyProviders){
				try{				
					currenttip = dataProvider.getWailaBody(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
				}
			}
		}
		else if (layout == Layout.FOOTER){
			for (IWailaDataProvider dataProvider : tailProviders){
				try{				
					currenttip = dataProvider.getWailaTail(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
				}
			}
		}
		
		return currenttip;
	}

	public boolean areMopDifferent(EntityPlayer player, MovingObjectPosition mop){
		return this.prevMOP.blockX != mop.blockX || this.prevMOP.blockY != mop.blockY || this.prevMOP.blockZ != mop.blockZ || prevWorldID != player.dimension;
	}
}
