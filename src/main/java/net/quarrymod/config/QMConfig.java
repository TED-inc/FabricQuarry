package net.quarrymod.config;

import java.util.Arrays;
import java.util.List;
import reborncore.common.config.Config;

public class QMConfig {

    @Config(config = "machines", category = "quarry", key = "QuarryMaxInput", comment = "Quarry Max Input (Value in EU)")
    public static int quarryMaxInput = 128;

    @Config(config = "machines", category = "quarry", key = "QuarryMaxInputOverclockerMultipier", comment = "Quarry Max Input Multiplier by overclocker upgrades")
    public static double quarryMaxInputOverclockerMultipier = 6;

    @Config(config = "machines", category = "quarry", key = "QuarryMaxEnergy", comment = "Quarry Max Energy (Value in EU)")
    public static int quarryMaxEnergy = 100_000;

    @Config(config = "machines", category = "quarry", key = "QuarryEnergyPerExcavation", comment = "Quarry Energy Per Excavation (Value in EU)")
    public static int quarryEnergyPerExcavation = 6_000;

    @Config(config = "machines", category = "quarry", key = "QuarryTiksPerExcavation", comment = "Quarry Tiks Per Excavation, 20 ticks - 1 second")
    public static int quarryTiksPerExcavation = 60;

    @Config(config = "machines", category = "quarry", key = "QuarryMinTiksPerExcavation", comment = "Quarry Min Tiks Per Excavation (with all 4 upgrdes), 20 ticks - 1 second")
    public static int quarryMinTiksPerExcavation = 8;

    @Deprecated
    @Config(config = "machines", category = "quarry", key = "QuarrySqrWorkRadius", comment = "Do not affect anything anymore")
    public static int quarrySqrWorkRadius = 8;

    @Config(config = "machines", category = "quarry", key = "QuarryExtenderWorkRadius", comment = "Quarry Extender Work Radius, added radius to SqrWorkRadius for each level, in blocks")
    public static List<Double> quarrySqrWorkRadiusByUpgradeLevel = Arrays.asList(7.0, 12.0, 18.0, 24.0);

    @Config(config = "machines", category = "quarry", key = "QuarryAccessibleExcavationModes", comment = "Quarry Accessible Excavation Modes, 1 - ores only, 2 - all only, 3 - all and ores")
    public static int quarryAccessibleExcavationModes = 3;

    @Config(config = "machines", category = "quarry", key = "QuarryAdditioanlBlocksToMine", comment = "Additioanl Blocks to Mine list")
    public static List<String> quarryAdditioanlBlocksToMine = Arrays.asList("minecraft:ancient_debris");
}
