package mcp.mobius.waila;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.commands.CommandDumpHandlers;
import mcp.mobius.waila.network.MessageReceiveData;
import mcp.mobius.waila.network.MessageRequestEntity;
import mcp.mobius.waila.network.MessageRequestTile;
import mcp.mobius.waila.network.MessageServerPing;
import mcp.mobius.waila.proxy.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Set;

@Mod(modid = Waila.MODID, name = Waila.NAME, version = Waila.VERSION, acceptableRemoteVersions = "*", guiFactory = "mcp.mobius.waila.gui.ConfigGuiFactory")
public class Waila {

    public static final String MODID = "waila";
    public static final String NAME = "Waila";
    public static final String VERSION = "@VERSION@";
    public static final Logger LOGGER = LogManager.getLogger("Waila");
    public static final SimpleNetworkWrapper NETWORK_WRAPPER = new SimpleNetworkWrapper(MODID);

    // The instance of your mod that Forge uses.
    @Instance(MODID)
    public static Waila instance;
    @SidedProxy(clientSide = "mcp.mobius.waila.proxy.ProxyClient", serverSide = "mcp.mobius.waila.proxy.ProxyServer")
    public static IProxy proxy;
    public static Set<ASMDataTable.ASMData> plugins;
    public static File configDir;
    public static File themeDir;

    public boolean serverPresent = false;

    /* INIT SEQUENCE */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NETWORK_WRAPPER.registerMessage(MessageServerPing.Handler.class, MessageServerPing.class, 0, Side.CLIENT);
        NETWORK_WRAPPER.registerMessage(MessageRequestTile.Handler.class, MessageRequestTile.class, 1, Side.SERVER);
        NETWORK_WRAPPER.registerMessage(MessageRequestEntity.Handler.class, MessageRequestEntity.class, 2, Side.SERVER);
        NETWORK_WRAPPER.registerMessage(MessageReceiveData.Handler.class, MessageReceiveData.class, 3, Side.CLIENT);

        proxy.preInit(event);
    }

    @EventHandler
    public void initialize(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        proxy.loadComplete(event);
    }

    @EventHandler
    public void processIMC(FMLInterModComms.IMCEvent event) {
        for (IMCMessage imcMessage : event.getMessages()) {
            if (!imcMessage.isStringMessage()) continue;

            if (imcMessage.key.equalsIgnoreCase("addconfig")) {
                String[] params = imcMessage.getStringValue().split("\\$\\$");
                if (params.length != 3) {
                    Waila.LOGGER.warn(String.format("Error while parsing config option from [ %s ] for %s", imcMessage.getSender(), imcMessage.getStringValue()));
                    continue;
                }
                Waila.LOGGER.info(String.format("Receiving config request from [ %s ] for %s", imcMessage.getSender(), imcMessage.getStringValue()));
                ConfigHandler.instance().addConfig(params[0], params[1], params[2]);
            }

            if (imcMessage.key.equalsIgnoreCase("register")) {
                Waila.LOGGER.info(String.format("Receiving registration request from [ %s ] for method %s", imcMessage.getSender(), imcMessage.getStringValue()));
                ModuleRegistrar.instance().addIMCRequest(imcMessage.getStringValue(), imcMessage.getSender());
            }
        }
    }


    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandDumpHandlers());
    }
}