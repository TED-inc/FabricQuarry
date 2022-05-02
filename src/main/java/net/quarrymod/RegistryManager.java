package net.quarrymod;

import static reborncore.RebornRegistry.registerBlock;
import static reborncore.RebornRegistry.registerItem;

import java.util.Arrays;
import net.minecraft.item.Item.Settings;
import net.minecraft.util.Identifier;
import net.quarrymod.events.StackToolTipHandler;
import net.quarrymod.init.QMBlockEntities;
import net.quarrymod.init.QuarryManagerContent;
import net.quarrymod.init.QuarryManagerContent.Machine;
import net.quarrymod.init.QuarryManagerContent.Upgrades;
import techreborn.TechReborn;

public class RegistryManager {

    private static Settings itemGroupSettings;

    public static Settings getItemGroupSettings() {
        return itemGroupSettings;
    }

    public static void Init() {
        itemGroupSettings = new Settings().group(TechReborn.ITEMGROUP);

        registerBlock(QuarryManagerContent.DRILL_TUBE,
            itemGroupSettings,
            new Identifier(QuarryMod.MOD_ID, "drill_tube"));

        Arrays.stream(Machine.values()).forEach(
            value ->
                registerBlock(value.block,
                    itemGroupSettings,
                    new Identifier(QuarryMod.MOD_ID, value.name)));

        Arrays.stream(Upgrades.values()).forEach(
            value -> registerItem(value.item, new Identifier(QuarryMod.MOD_ID, value.name)));
        QMBlockEntities.init();
    }

    @SuppressWarnings("MethodCallSideOnly")
    public static void ClientInit() {
        StackToolTipHandler.setup();
    }
}
