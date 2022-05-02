package net.quarrymod;

import java.util.Arrays;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.util.Identifier;
import net.quarrymod.events.StackToolTipHandler;
import net.quarrymod.init.QMBlockEntities;
import net.quarrymod.init.QMContent;
import net.quarrymod.init.QMContent.Machine;
import net.quarrymod.init.QMContent.Upgrades;
import reborncore.RebornRegistry;
import techreborn.TechReborn;

public class RegistryManager {

    public static Settings itemGroup;

    public static void Init() {
        itemGroup = new Item.Settings().group(TechReborn.ITEMGROUP);
        RebornRegistry.registerBlock(QMContent.DRILL_TUBE, itemGroup, new Identifier(QuarryMod.MOD_ID, "drill_tube"));

        Arrays.stream(Machine.values()).forEach(value -> RebornRegistry.registerBlock(
            value.block,
            itemGroup,
            new Identifier(QuarryMod.MOD_ID, value.name)));

        Arrays.stream(Upgrades.values()).forEach(value -> RebornRegistry.registerItem(
            value.item,
            new Identifier(QuarryMod.MOD_ID, value.name)));

        QMBlockEntities.init();
    }

    @SuppressWarnings("MethodCallSideOnly")
    public static void ClientInit() {
        StackToolTipHandler.setup();
    }
}
