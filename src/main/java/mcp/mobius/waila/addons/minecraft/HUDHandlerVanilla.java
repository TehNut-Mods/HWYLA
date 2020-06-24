package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.*;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.SilverfishBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ComparatorMode;
import net.minecraft.tileentity.JukeboxTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HUDHandlerVanilla implements IComponentProvider, IServerDataProvider<TileEntity> {

    static final HUDHandlerVanilla INSTANCE = new HUDHandlerVanilla();

    static final ResourceLocation OBJECT_NAME_TAG = new ResourceLocation(Waila.MODID, "object_name");

    @Override
    public ItemStack getStack(IDataAccessor accessor, IPluginConfig config) {
        if (config.get(PluginMinecraft.CONFIG_HIDE_SILVERFISH) && accessor.getBlock() instanceof SilverfishBlock)
            return new ItemStack(((SilverfishBlock) accessor.getBlock()).getMimickedBlock().asItem());

        if (accessor.getBlock() == Blocks.WHEAT)
            return new ItemStack(Items.WHEAT);

        if (accessor.getBlock() == Blocks.BEETROOTS)
            return new ItemStack(Items.BEETROOT);

        return ItemStack.EMPTY;
    }

    @Override
    public void appendHead(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (config.get(PluginMinecraft.CONFIG_HIDE_SILVERFISH) && accessor.getBlock() instanceof SilverfishBlock)
            ((ITaggableList<ResourceLocation, ITextComponent>) tooltip).setTag(OBJECT_NAME_TAG, formatBlockName(accessor.getStack().getTranslationKey()));

        if (accessor.getBlock() == Blocks.SPAWNER && config.get(PluginMinecraft.CONFIG_SPAWNER_TYPE)) {
            TextComponent blockName = formatBlockName(accessor.getBlock().getTranslationKey());
            Entity entity = ((MobSpawnerTileEntity) accessor.getTileEntity()).getSpawnerBaseLogic().getCachedEntity();
            if (entity != null)
                blockName.appendText(" (").appendSibling(entity.getDisplayName()).appendText(")");
            ((ITaggableList<ResourceLocation, ITextComponent>) tooltip).setTag(OBJECT_NAME_TAG, blockName);
        }

        if (config.get(PluginMinecraft.POTTED_PLANTS) && accessor.getBlock() instanceof FlowerPotBlock && accessor.getBlock() != ((FlowerPotBlock) accessor.getBlock()).getEmptyPot())
            ((ITaggableList<ResourceLocation, ITextComponent>) tooltip).setTag(OBJECT_NAME_TAG, formatBlockName(accessor.getBlock().getTranslationKey()));
    }

    private static TextComponent formatBlockName(String translationKey) {
        return new StringTextComponent(String.format(Waila.CONFIG.get().getFormatting().getBlockName(), new TranslationTextComponent(translationKey).getFormattedText()));
    }

    private static final Map<String, Item> RECORD_CACHE = new HashMap<>();
    private static Item lookupRecord(String record) {
        return RECORD_CACHE.computeIfAbsent(record, s -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(s)));
    }

    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (config.get(PluginMinecraft.CONFIG_CROP_PROGRESS)) {
            if (accessor.getBlock() instanceof CropsBlock ) {
                CropsBlock crop = (CropsBlock ) accessor.getBlock();
                addMaturityTooltip(tooltip, accessor.getBlockState().get(crop.getAgeProperty()) / (float) crop.getMaxAge());
            } else if (accessor.getBlock() == Blocks.MELON_STEM || accessor.getBlock() == Blocks.PUMPKIN_STEM) {
                addMaturityTooltip(tooltip, accessor.getBlockState().get(BlockStateProperties.AGE_0_7) / 7F);
            } else if (accessor.getBlock() == Blocks.COCOA) {
                addMaturityTooltip(tooltip, accessor.getBlockState().get(BlockStateProperties.AGE_0_2) / 2.0F);
            }
        }

        if (config.get(PluginMinecraft.CONFIG_LEVER) && accessor.getBlock() instanceof LeverBlock) {
            boolean active = accessor.getBlockState().get(BlockStateProperties.POWERED);
            tooltip.add(new TranslationTextComponent("tooltip.waila.state", new TranslationTextComponent("tooltip.waila.state_" + (active ? "on" : "off"))));
            return;
        }

        if (config.get(PluginMinecraft.CONFIG_REPEATER) && accessor.getBlock() == Blocks.REPEATER) {
            int delay = accessor.getBlockState().get(BlockStateProperties.DELAY_1_4);
            tooltip.add(new TranslationTextComponent("waila.tooltip.delay", delay));
            return;
        }

        if (config.get(PluginMinecraft.CONFIG_COMPARATOR) && accessor.getBlock() == Blocks.COMPARATOR) {
            ComparatorMode mode = accessor.getBlockState().get(BlockStateProperties.COMPARATOR_MODE);
            tooltip.add(new TranslationTextComponent("tooltip.waila.mode", new TranslationTextComponent("tooltip.waila.mode_." + (mode == ComparatorMode.COMPARE ? "comparator" : "subtractor"))));
            return;
        }

        if (config.get(PluginMinecraft.CONFIG_REDSTONE) && accessor.getBlock() == Blocks.REDSTONE_WIRE) {
            tooltip.add(new TranslationTextComponent("tooltip.waila.power", accessor.getBlockState().get(BlockStateProperties.POWER_0_15)));
            return;
        }

        if (config.get(PluginMinecraft.CONFIG_JUKEBOX) && accessor.getBlock() == Blocks.JUKEBOX) {
            if (accessor.getServerData().contains("record", Constants.NBT.TAG_STRING)) {
                Item record = lookupRecord(accessor.getServerData().getString("record"));
                tooltip.add(new TranslationTextComponent("record.nowPlaying", record instanceof MusicDiscItem ? ((MusicDiscItem) record).getRecordDescription() : "???"));
            } else
                tooltip.add(new TranslationTextComponent("tooltip.waila.empty"));
        }
    }

    @Override
    public void appendServerData(CompoundNBT data, ServerPlayerEntity player, World world, TileEntity blockEntity) {
        if (blockEntity instanceof JukeboxTileEntity) {
            ItemStack record = ((JukeboxTileEntity) blockEntity).getRecord();
            if (!record.isEmpty() && record.getItem().getRegistryName() != null)
                data.putString("record", record.getItem().getRegistryName().toString());
        }
    }

    private static void addMaturityTooltip(List<ITextComponent> tooltip, float growthValue) {
        growthValue *= 100.0F;
        if (growthValue < 100.0F)
            tooltip.add(new TranslationTextComponent("tooltip.waila.crop_growth", String.format("%.0f%%", growthValue)));
        else
            tooltip.add(new TranslationTextComponent("tooltip.waila.crop_growth", new TranslationTextComponent("tooltip.waila.crop_mature")));
    }
}
