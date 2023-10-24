package net.quarrymod.events;

import static net.quarrymod.utils.ToolTipAssistUtils.getUpgradeStats;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.registry.Registries;
import net.quarrymod.QuarryMod;
import net.quarrymod.init.QuarryManagerContent;
import net.quarrymod.items.QuarryUpgradeItem;

public class StackToolTipHandler implements ItemTooltipCallback {

    public static final Map<Item, Boolean> IS_QM_ITEM_CACHE = Maps.newHashMap();

    public static void setup() {
        ItemTooltipCallback.EVENT.register(new StackToolTipHandler());
    }

    @Override
    public void getTooltip(ItemStack stack, TooltipContext tooltipContext, List<Text> tooltipLines) {
        Item item = stack.getItem();

        if (!MinecraftClient.getInstance().isOnThread()) {
            return;
        }
        if (!isQMItem(item)) {
            return;
        }

        if (item instanceof QuarryUpgradeItem quarryItem) {
            tooltipLines.addAll(
                getUpgradeStats(
                    QuarryManagerContent.Upgrades.getFrom(quarryItem),
                    Screen.hasShiftDown()));
        }
    }

    private static boolean isQMItem(Item item) {
        return IS_QM_ITEM_CACHE.computeIfAbsent(item,
            b -> Registries.ITEM.getId(item).getNamespace().equals(QuarryMod.MOD_ID));
    }
}
