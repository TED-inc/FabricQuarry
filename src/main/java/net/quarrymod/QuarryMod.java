package net.quarrymod;

import net.fabricmc.api.ModInitializer;

public class QuarryMod implements ModInitializer {

	public static final String MOD_ID = "quarrymod";
    public static final String MOD_NAME = "QuarryMod";

	@Override
	public void onInitialize() {
		RegistryManager.Init();
	}
}
