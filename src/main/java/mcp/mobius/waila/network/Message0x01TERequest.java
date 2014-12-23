package mcp.mobius.waila.network;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;

import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.DataAccessorBlockServer;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.AccessHelper;
import mcp.mobius.waila.utils.NBTUtil;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class Message0x01TERequest extends SimpleChannelInboundHandler<Message0x01TERequest> implements IWailaMessage {
	
	private static Field classToNameMap = null;
	
	static{
		try{
			classToNameMap = TileEntity.class.getDeclaredField("classToNameMap");
			classToNameMap.setAccessible(true);
		} catch (Exception e){
			
			try{
				classToNameMap = TileEntity.class.getDeclaredField("field_145853_j");
				classToNameMap.setAccessible(true);
			} catch (Exception f){
				throw new RuntimeException(f);
			}
			
		}
	}
	
	public int dim;
	public int posX;
	public int posY;
	public int posZ;	
	public double vecX;
	public double vecY;
	public double vecZ;
	public EnumFacing sideHit;
	
	public Message0x01TERequest(){}	
	
	public Message0x01TERequest(TileEntity ent, MovingObjectPosition mop){
		this.dim     = ent.getWorld().provider.getDimensionId();
		this.posX    = ent.getPos().getX();
		this.posY    = ent.getPos().getY();
		this.posZ    = ent.getPos().getZ();
		this.vecX    = mop.hitVec.xCoord;
		this.vecY    = mop.hitVec.yCoord;
		this.vecZ    = mop.hitVec.zCoord;
		this.sideHit = mop.sideHit;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, IWailaMessage msg, ByteBuf target) throws Exception {
		target.writeInt(dim);
		target.writeInt(posX);
		target.writeInt(posY);
		target.writeInt(posZ);
		target.writeDouble(vecX);
		target.writeDouble(vecY);
		target.writeDouble(vecZ);
		target.writeInt(sideHit.ordinal());
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IWailaMessage rawmsg) {
		try{
			Message0x01TERequest msg = (Message0x01TERequest)rawmsg;
			msg.dim     = dat.readInt();
			msg.posX    = dat.readInt();
			msg.posY    = dat.readInt();
			msg.posZ    = dat.readInt();
			msg.vecX    = dat.readDouble();
			msg.vecY    = dat.readDouble();
			msg.vecZ    = dat.readDouble();
			msg.sideHit = EnumFacing.values()[dat.readInt()];
		
		}catch (Exception e){
			WailaExceptionHandler.handleErr(e, this.getClass().toString(), null);
		}		
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message0x01TERequest msg) throws Exception {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        World           world  = DimensionManager.getWorld(msg.dim);
        BlockPos        pos    = new BlockPos(msg.posX, msg.posY, msg.posZ);
        TileEntity      entity = world.getTileEntity(pos);
        
        if (entity != null){
        	
        	try{
        		DataAccessorBlockServer accessor = new DataAccessorBlockServer(
        				msg.dim, 
        				world, 
        				pos, 
        				((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).playerEntity,
        				new Vec3(msg.vecX, msg.vecY, msg.vecZ),
        				msg.sideHit,
        				entity);
        		
        		NBTTagCompound tag  = new NBTTagCompound();
        		boolean hasNBTBlock = ModuleRegistrar.instance().hasNBTProviders(accessor.getBlock());
        		boolean hasNBTEnt   = ModuleRegistrar.instance().hasNBTProviders(entity);

        		if (hasNBTBlock || hasNBTEnt){
        			tag.setInteger("x", msg.posX);
            		tag.setInteger("y", msg.posY);
            		tag.setInteger("z", msg.posZ);
            		tag.setString ("id", (String)((HashMap)classToNameMap.get(null)).get(entity.getClass()));
            		

            		
        			for (IWailaDataProvider provider : ModuleRegistrar.instance().getNBTProviders(accessor.getBlock())){
    					tag = provider.getNBTData(entity, tag, accessor);
        			}
        			
        			for (IWailaDataProvider provider : ModuleRegistrar.instance().getNBTProviders(entity)){
        				tag = provider.getNBTData(entity, tag, accessor);
        			}
        			
        			tag.setInteger("WailaX", msg.posX);
            		tag.setInteger("WailaY", msg.posY);
            		tag.setInteger("WailaZ", msg.posZ);
            		tag.setString ("WailaID", (String)((HashMap)classToNameMap.get(null)).get(entity.getClass()));        		
            		
        			ctx.writeAndFlush(new Message0x02TENBTData(tag)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);        			
        		}
        	}catch(Throwable e){
        		e.printStackTrace();
        		WailaExceptionHandler.handleErr(e, entity.getClass().toString(), null);
        	}
        }
		
		
	}
}

