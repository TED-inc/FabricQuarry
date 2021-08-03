package net.quarrymod.config;

import reborncore.common.config.Config;

public class QMConfig {
    
    @Config(config = "machines", category = "quarry", key = "QuarryMaxInput", comment = "Quarry Max Input (Value in EU)")
	public static int quarryMaxInput = 32;

	@Config(config = "machines", category = "quarry", key = "QuarryMaxEnergy", comment = "Quarry Max Energy (Value in EU)")
	public static int quarryMaxEnergy = 1_000;
}
