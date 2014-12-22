package mcp.mobius.waila.network;

import java.util.HashSet;

import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.AccessHelper;
import mcp.mobius.waila.utils.NBTUtil;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
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
	
	public Message0x03EntRequest(){}	
	
	public Message0x03EntRequest(Entity ent){
		this.dim  = ent.worldObj.provider.getDimensionId();
		this.id   = ent.getEntityId();
	}	
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, IWailaMessage msg, ByteBuf target) throws Exception {
		target.writeInt(dim);
		target.writeInt(id);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IWailaMessage rawmsg) {
		try{
			Message0x03EntRequest msg = (Message0x03EntRequest)rawmsg;
			msg.dim  = dat.readInt();
			msg.id   = dat.readInt();
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
        		NBTTagCompound tag = new NBTTagCompound();
        		
        		EntityPlayerMP player = ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).playerEntity;
        		
        		if (ModuleRegistrar.instance().hasNBTEntityProviders(entity)){
        			for (IWailaEntityProvider provider : ModuleRegistrar.instance().getNBTEntityProviders(entity)){
        				try{
        					tag = provider.getNBTData(player, entity, tag, world);
        				} catch (AbstractMethodError ame){
        					tag = AccessHelper.getNBTData(provider, entity, tag);
        				}        				
        			}
            		ctx.writeAndFlush(new Message0x04EntNBTData(tag)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        		}
        		
        	}catch(Throwable e){
        		WailaExceptionHandler.handleErr(e, entity.getClass().toString(), null);
        	}
        }		
	}

}
