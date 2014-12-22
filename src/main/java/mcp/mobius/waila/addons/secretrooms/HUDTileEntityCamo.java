package mcp.mobius.waila.addons.secretrooms;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mcp.mobius.waila.api.ITaggedList.ITipList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDTileEntityCamo implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		/*
		if (!config.getConfig("secretrooms.hide")) return null;
		
		if ( SecretRoomsModule.BlockCamoTrapDoor.isInstance(accessor.getBlock()) ){
			return this.getTrapDoorBlock(accessor);

		}

		else if ( SecretRoomsModule.BlockTorchLever.isInstance(accessor.getBlock()) ){
			return new ItemStack(Blocks.torch);
		}		
		
		else if (accessor.getNBTData() != null){
			NBTTagCompound tag = accessor.getNBTData();
			
			if (tag.hasKey("copyID") && tag.hasKey("copyMeta")){
				int copyID   = accessor.getNBTInteger(tag, "copyID");
				int copyMeta = accessor.getNBTInteger(tag, "copyMeta");			
				return new ItemStack(copyID, 1, copyMeta);
			}
		}
		*/
		return null;
	}

	@Override
	public ITipList getWailaHead(ItemStack itemStack, ITipList currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public ITipList getWailaBody(ItemStack itemStack, ITipList currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public ITipList getWailaTail(ItemStack itemStack, ITipList currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	private ItemStack getTrapDoorBlock(IWailaDataAccessor accessor){
		// modify coordinates to get hinge Block.
		/*
		int i = accessor.getMetadata();
		int j = accessor.getPosition().blockX;
		int k = accessor.getPosition().blockZ;

		if ((i & 3) == 0)
		{
			k++;
		}

		if ((i & 3) == 1)
		{
			k--;
		}

		if ((i & 3) == 2)
		{
			j++;
		}

		if ((i & 3) == 3)
		{
			j--;
		}

		int blockID   = accessor.getWorld().getBlockId(j, accessor.getPosition().blockY, k);
		int blockMeta = accessor.getWorld().getBlockMetadata(j, accessor.getPosition().blockY, k);
		
		return new ItemStack(blockID, 1, blockMeta);
		*/
		return null;
	}
	
	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		if (te != null)
			te.writeToNBT(tag);
		return tag;
	}	
	
}
