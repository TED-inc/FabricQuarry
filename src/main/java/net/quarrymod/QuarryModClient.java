package net.quarrymod;

import net.fabricmc.api.ClientModInitializer;

public class QuarryModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        RegistryManager.ClientInit();
    }
}
