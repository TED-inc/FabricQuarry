package net.quarrymod;

import net.fabricmc.api.ModInitializer;
import net.quarrymod.config.QuarryMachineConfig;
import net.quarrymod.packets.QuarryManagerServerPacket;
import reborncore.common.config.Configuration;

public class QuarryMod implements ModInitializer {

    public static final String MOD_ID = "quarrymod";

    @Override
    public void onInitialize() {
        new Configuration(QuarryMachineConfig.class, MOD_ID);
        RegistryManager.Init();
        QuarryManagerServerPacket.init();
    }
}
