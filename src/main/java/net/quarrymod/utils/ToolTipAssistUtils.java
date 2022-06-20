package net.quarrymod.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.quarrymod.config.QuarryMachineConfig;
import net.quarrymod.init.QuarryManagerContent;

public class ToolTipAssistUtils {

    public static List<Text> getUpgradeStats(QuarryManagerContent.Upgrades upgradeType, boolean shiftHeld) {
        List<Text> tips = new ArrayList<>();

        switch (upgradeType) {
            case RANGE_EXTENDER_LVL1 -> getTextForRangeExtender(1, tips);
            case RANGE_EXTENDER_LVL2 -> getTextForRangeExtender(2, tips);
            case RANGE_EXTENDER_LVL3 -> getTextForRangeExtender(3, tips);
            default -> tips.add(
                Text.translatable("tooltip.quarrymod." + upgradeType.name)
                    .formatted(Formatting.GOLD));
        }

        if (shiftHeld && upgradeType.name.contains("lvl")) {
            tips.add(Text.of(""));
            String translation = I18n.translate("tooltip.quarrymod.upgrade_leveled_warining");
            Arrays.stream(translation.split("\n"))
                .forEach(line -> tips.add(Text.of(line.formatted(Formatting.RED))));
        }

        return tips;
    }

    private static void getTextForRangeExtender(int level, List<Text> tips) {
        tips.add(Text.translatable("tooltip.quarrymod.range_extender_effect")
            .formatted(Formatting.GOLD));
        tips.add(Text.translatable("tooltip.quarrymod.range_extender_value")
            .formatted(Formatting.GREEN)
            .append(Text.of(String.valueOf(QuarryMachineConfig.quarrySqrWorkRadiusByUpgradeLevel.get(level).intValue())
                .formatted(Formatting.GOLD)))
            .append(Text.translatable("tooltip.quarrymod.range_extender_blocks")
                .formatted(Formatting.GOLD)));
    }
}
