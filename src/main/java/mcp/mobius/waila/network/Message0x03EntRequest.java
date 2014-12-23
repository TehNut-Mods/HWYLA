package mcp.mobius.waila.network;

import java.util.HashSet;

import mcp.mobius.waila.api.IWailaEntityAccessorServer;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.impl.DataAccessorEntityServer;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.AccessHelper;
import mcp.mobius.waila.utils.NBTUtil;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
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

public class Message0x03EntRequest extends SimpleChannelInboundHandler<Message0x03EntRequest> implements IWailaMessage {

	public int dim;
	public int id;
	public double vecX;
	public double vecY;
	public double vecZ;
	public EnumFacing sideHit;
	
	public Message0x03EntRequest(){}	
	
	public Message0x03EntRequest(Entity ent, MovingObjectPosition mop){
		this.dim  = ent.worldObj.provider.getDimensionId();
		this.id   = ent.getEntityId();
		this.vecX    = mop.hitVec.xCoord;
		this.vecY    = mop.hitVec.yCoord;
		this.vecZ    = mop.hitVec.zCoord;
		this.sideHit = mop.sideHit;		
	}	
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, IWailaMessage msg, ByteBuf target) throws Exception {
		target.writeInt(dim);
		target.writeInt(id);
		target.writeDouble(vecX);
		target.writeDouble(vecY);
		target.writeDouble(vecZ);
		target.writeInt(sideHit.ordinal());		
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IWailaMessage rawmsg) {
		try{
			Message0x03EntRequest msg = (Message0x03EntRequest)rawmsg;
			msg.dim  = dat.readInt();
			msg.id   = dat.readInt();
			msg.vecX    = dat.readDouble();
			msg.vecY    = dat.readDouble();
			msg.vecZ    = dat.readDouble();
			msg.sideHit = EnumFacing.values()[dat.readInt()];			
		}catch (Exception e){
			WailaExceptionHandler.handleErr(e, this.getClass().toString(), null);
		}		
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message0x03EntRequest msg) throws Exception {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        World           world  = DimensionManager.getWorld(msg.dim);
        Entity          entity = world.getEntityByID(msg.id);
        
        if (entity != null){
        	try{
        		
        		IWailaEntityAccessorServer accessor = new DataAccessorEntityServer(
        				msg.dim,
        				world,
        				((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).playerEntity,
        				new Vec3(msg.vecX, msg.vecY, msg.vecZ),
        				msg.sideHit,
        				entity        				
        				);
        		
        		NBTTagCompound tag = new NBTTagCompound();

        		
        		if (ModuleRegistrar.instance().hasNBTEntityProviders(entity)){
        			for (IWailaEntityProvider provider : ModuleRegistrar.instance().getNBTEntityProviders(entity)){
        				tag = provider.getNBTData(entity, tag, accessor);
        			}
            		ctx.writeAndFlush(new Message0x04EntNBTData(tag)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        		}
        		
        	}catch(Throwable e){
        		WailaExceptionHandler.handleErr(e, entity.getClass().toString(), null);
        	}
        }		
	}

}
