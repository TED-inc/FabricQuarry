package net.quarrymod;

import net.fabricmc.api.ModInitializer;
import net.quarrymod.config.QMConfig;
import net.quarrymod.packets.QMServerboundPackets;
import reborncore.common.config.Configuration;

public class QuarryMod implements ModInitializer {

    public static final String MOD_ID = "quarrymod";

    @Override
    public void onInitialize() {
        new Configuration(QMConfig.class, MOD_ID);
        RegistryManager.Init();
        QMServerboundPackets.init();
    }
}
