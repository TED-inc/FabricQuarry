package net.quarrymod.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

@Environment(EnvType.CLIENT)
public class QuarryScreenRegistry {

    private QuarryScreenRegistry() {
        // Left empty to hide the original constructor
    }

    public static void init() {
        ScreenRegistry.register(GuiType.QUARRY.getType(), GuiType.QUARRY.getGuiFactory());
    }
}
