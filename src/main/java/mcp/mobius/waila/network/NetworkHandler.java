package mcp.mobius.waila.network;

import io.netty.buffer.Unpooled;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.config.ConfigEntry;
import mcp.mobius.waila.api.impl.WailaRegistrar;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Set;

public class NetworkHandler {

    public static final Identifier REQUEST_ENTITY = new Identifier(Waila.MODID, "request_entity");
    public static final Identifier REQUEST_TILE = new Identifier(Waila.MODID, "request_tile");

    public static void init() {
        ServerSidePacketRegistry.INSTANCE.register(REQUEST_ENTITY, (packetContext, packetByteBuf) -> {
            PlayerEntity player = packetContext.getPlayer();
            World world = player.world;
            Entity entity = world.getEntityById(packetByteBuf.readInt());
            packetContext.getTaskQueue().execute(() -> {
                if (entity == null)
                    return;

                CompoundTag tag = new CompoundTag();
                if (WailaRegistrar.INSTANCE.hasNBTEntityProviders(entity)) {
                    WailaRegistrar.INSTANCE.getNBTEntityProviders(entity).values().forEach(l -> l.forEach(p -> p.appendServerData(tag, (ServerPlayerEntity) player, world, (LivingEntity) entity)));
                } else {
                    entity.toTag(tag);
                }

                tag.putInt("WailaEntityID", entity.getEntityId());

                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeCompoundTag(tag);
                ((ServerPlayerEntity) player).networkHandler.sendPacket(new CustomPayloadS2CPacket(ClientNetworkHandler.RECEIVE_DATA, buf));
            });
        });
        ServerSidePacketRegistry.INSTANCE.register(REQUEST_TILE, (packetContext, packetByteBuf) -> {
            PlayerEntity player = packetContext.getPlayer();
            World world = player.world;
            BlockPos pos = packetByteBuf.readBlockPos();

            packetContext.getTaskQueue().execute(() -> {
                if (!world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4))
                    return;

                BlockEntity tile = world.getBlockEntity(pos);
                if (tile == null)
                    return;

                BlockState state = world.getBlockState(pos);

                CompoundTag tag = new CompoundTag();
                if (WailaRegistrar.INSTANCE.hasNBTProviders(tile) || WailaRegistrar.INSTANCE.hasNBTProviders(state.getBlock())) {
                    WailaRegistrar.INSTANCE.getNBTProviders(tile).values().forEach(l -> l.forEach(p -> p.appendServerData(tag, (ServerPlayerEntity) player, world, tile)));
                    WailaRegistrar.INSTANCE.getNBTProviders(state.getBlock()).values().forEach(l -> l.forEach(p -> p.appendServerData(tag, (ServerPlayerEntity) player, world, tile)));
                } else {
                    tile.toTag(tag);
                }

                tag.putInt("x", pos.getX());
                tag.putInt("y", pos.getY());
                tag.putInt("z", pos.getZ());
                tag.putString("id", Registry.BLOCK_ENTITY_TYPE.getId(tile.getType()).toString());

                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeCompoundTag(tag);
                ((ServerPlayerEntity) player).networkHandler.sendPacket(new CustomPayloadS2CPacket(ClientNetworkHandler.RECEIVE_DATA, buf));
            });
        });
    }

    @Environment(EnvType.CLIENT)
    public static void requestEntity(Entity entity) {
        if (MinecraftClient.getInstance().getNetworkHandler() == null)
            return;

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getEntityId());
        MinecraftClient.getInstance().getNetworkHandler().getConnection().send(new CustomPayloadC2SPacket(REQUEST_ENTITY, buf));
    }

    @Environment(EnvType.CLIENT)
    public static void requestTile(BlockEntity blockEntity) {
        if (MinecraftClient.getInstance().getNetworkHandler() == null)
            return;

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(blockEntity.getPos());
        MinecraftClient.getInstance().getNetworkHandler().getConnection().send(new CustomPayloadC2SPacket(REQUEST_TILE, buf));
    }

    @Environment(EnvType.SERVER)
    public static void sendConfig(PluginConfig config, ServerPlayerEntity player) {
        Waila.LOGGER.info("Sending config to {} ({})", player.getGameProfile().getName(), player.getGameProfile().getId());
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        Set<ConfigEntry> entries = config.getSyncableConfigs();
        buf.writeInt(entries.size());
        entries.forEach(e -> {
            buf.writeInt(e.getId().toString().length());
            buf.writeString(e.getId().toString());
            buf.writeBoolean(e.getValue());
        });

        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(ClientNetworkHandler.GET_CONFIG, buf));
    }
}
