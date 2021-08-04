package net.quarrymod.config;

import reborncore.common.config.Config;

public class QMConfig {
    
    @Config(config = "machines", category = "quarry", key = "QuarryMaxInput", comment = "Quarry Max Input (Value in EU)")
	public static int quarryMaxInput = 128;

	@Config(config = "machines", category = "quarry", key = "QuarryMaxEnergy", comment = "Quarry Max Energy (Value in EU)")
	public static int quarryMaxEnergy = 100_000;

    @Config(config = "machines", category = "quarry", key = "QuarryEnergyPerExcavation", comment = "Quarry Energy Per Excavation (Value in EU)")
    public static int quarryEnergyPerExcavation = 6_000;

    @Config(config = "machines", category = "quarry", key = "QuarryTiksPerExcavation", comment = "Quarry Tiks Per Excavation, 20 ticks - 1 second")
	public static int quarryTiksPerExcavation = 60;

    @Config(config = "machines", category = "quarry", key = "QuarrySqrWorkRadius", comment = "Quarry Sqr Work Radius, square radius of work, in blocks")
	public static int quarrySqrWorkRadius = 8;
}
