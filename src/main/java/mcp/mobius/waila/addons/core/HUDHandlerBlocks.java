package mcp.mobius.waila.addons.core;

import com.google.common.base.Strings;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITaggableList;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class HUDHandlerBlocks implements IComponentProvider {

    static final IComponentProvider INSTANCE = new HUDHandlerBlocks();
    static final ResourceLocation OBJECT_NAME_TAG = new ResourceLocation(Waila.MODID, "object_name");
    static final ResourceLocation REGISTRY_NAME_TAG = new ResourceLocation(Waila.MODID, "registry_name");
    static final ResourceLocation MOD_NAME_TAG = new ResourceLocation(Waila.MODID, "mod_name");

    @Override
    public void appendHead(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockState().getMaterial().isLiquid())
            return;

        ((ITaggableList<ResourceLocation, ITextComponent>) tooltip).setTag(OBJECT_NAME_TAG, new StringTextComponent(String.format(Waila.CONFIG.get().getFormatting().getBlockName(), accessor.getStack().getDisplayName().getString())));
        if (config.get(PluginCore.CONFIG_SHOW_REGISTRY))
            ((ITaggableList<ResourceLocation, ITextComponent>) tooltip).setTag(REGISTRY_NAME_TAG, new StringTextComponent(accessor.getBlock().getRegistryName().toString()).func_240701_a_(TextFormatting.GRAY));
    }

    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (config.get(PluginCore.CONFIG_SHOW_STATES)) {
            BlockState state = accessor.getBlockState();
            state.func_235904_r_().forEach(p -> {
                Comparable<?> value = state.get(p);
                ITextComponent valueText = new StringTextComponent(value.toString()).func_240701_a_(p instanceof BooleanProperty ? value == Boolean.TRUE ? TextFormatting.GREEN : TextFormatting.RED : TextFormatting.RESET);
                tooltip.add(new StringTextComponent(p.getName() + ":").func_230529_a_(valueText));
            });
        }
    }

    @Override
    public void appendTail(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        String modName = ModIdentification.getModInfo(accessor.getStack()).getName();
        if (!Strings.isNullOrEmpty(modName)) {
            modName = String.format(Waila.CONFIG.get().getFormatting().getModName(), modName);
            ((ITaggableList<ResourceLocation, ITextComponent>) tooltip).setTag(MOD_NAME_TAG, new StringTextComponent(modName));
        }
    }
}
