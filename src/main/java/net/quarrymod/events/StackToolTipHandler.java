package net.quarrymod.events;

import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import net.quarrymod.QuarryMod;
import net.quarrymod.init.QMContent;
import net.quarrymod.items.QuarryUpgradeItem;
import net.quarrymod.utils.ToolTipAssistUtils;

import java.util.List;
import java.util.Map;

public class StackToolTipHandler implements ItemTooltipCallback {

	public static final Map<Item, Boolean> ITEM_ID = Maps.newHashMap();

	public static void setup() {
		ItemTooltipCallback.EVENT.register(new StackToolTipHandler());
	}

	@Override
	public void getTooltip(ItemStack stack, TooltipContext tooltipContext, List<Text> tooltipLines) {
		Item item = stack.getItem();

		if (!MinecraftClient.getInstance().isOnThread() || !ITEM_ID.computeIfAbsent(item, StackToolTipHandler::isQMItem))
			return;

		if (item instanceof QuarryUpgradeItem)
			tooltipLines.addAll(
                ToolTipAssistUtils.getUpgradeStats(
                    QMContent.Upgrades.getFrom((QuarryUpgradeItem)item), 
                    stack.getCount(),
                    Screen.hasShiftDown()));
	}

	private static boolean isQMItem(Item item) {
		return Registry.ITEM.getId(item).getNamespace().equals(QuarryMod.MOD_ID);
	}
}
