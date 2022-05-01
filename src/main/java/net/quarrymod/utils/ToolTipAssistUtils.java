package net.quarrymod.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.quarrymod.config.QMConfig;
import net.quarrymod.init.QMContent;

public class ToolTipAssistUtils {

    public static List<Text> getUpgradeStats(QMContent.Upgrades upgradeType, int count, boolean shiftHeld) {
        List<Text> tips = new ArrayList<>();

        switch (upgradeType) {
            case RANGE_EXTENDER_LVL1:
                getTextForrangeExtender(1, tips);
                break;
            case RANGE_EXTENDER_LVL2:
                getTextForrangeExtender(2, tips);
                break;
            case RANGE_EXTENDER_LVL3:
                getTextForrangeExtender(3, tips);
                break;
            default:
                tips.add(
                    new TranslatableText("tooltip.quarrymod." + upgradeType.name)
                        .formatted(Formatting.GOLD));
        }

        if (shiftHeld && upgradeType.name.contains("lvl")) {
            tips.add(new LiteralText(""));
            String translation = I18n.translate("tooltip.quarrymod.upgrade_leveled_warining");
            Arrays.stream(translation.split("\n"))
                .forEach(line -> tips.add(new LiteralText(line).formatted(Formatting.RED)));
        }

        return tips;
    }

    private static void getTextForrangeExtender(int level, List<Text> tips) {
        tips.add(
            new TranslatableText("tooltip.quarrymod.range_extender_effect")
                .formatted(Formatting.GOLD));
        tips.add(
            new TranslatableText("tooltip.quarrymod.range_extender_value")
                .formatted(Formatting.GREEN)
                .append(
                    new LiteralText(String.valueOf(QMConfig.quarrySqrWorkRadiusByUpgradeLevel.get(level).intValue()))
                        .formatted(Formatting.GOLD))
                .append(new TranslatableText("tooltip.quarrymod.range_extender_blocks")
                    .formatted(Formatting.GOLD)));
    }
}
