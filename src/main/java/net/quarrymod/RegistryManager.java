package net.quarrymod;

import java.util.Arrays;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.quarrymod.init.QuarryModBlockEntities;
import net.quarrymod.items.QuarryModItemGroup;
import net.quarrymod.client.QuarryScreenRegistry;
import net.quarrymod.events.StackToolTipHandler;
import net.quarrymod.init.QuarryManagerContent;
import net.quarrymod.init.QuarryManagerContent.Machine;
import net.quarrymod.init.QuarryManagerContent.Upgrades;

public class RegistryManager {
    private static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(QuarryMod.MOD_ID, "item_group"));

    private static Settings itemGroupSettings;

    private RegistryManager() {
    }

    public static Settings getItemGroupSettings() {
        return itemGroupSettings;
    }

    public static void Init() {
        QuarryModItemGroup.registerItemGroups();

        itemGroupSettings = new Settings();

        registerBlock("drill_tube", QuarryManagerContent.DRILL_TUBE);

        Arrays.stream(Machine.values()).forEach(value -> registerBlock(value.name, value.block));

        Arrays.stream(Upgrades.values()).forEach(value -> registerItem(value.name, value.item));

        QuarryModBlockEntities.init();
    }

    private static void registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        Registry.register(Registries.BLOCK, new Identifier(QuarryMod.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Item item = new BlockItem(block, new FabricItemSettings());
        addItemGroupEntry(item);
        Registry.register(Registries.ITEM, new Identifier(QuarryMod.MOD_ID, name), item);
    }

    private static void registerItem(String name, Item item) {
        addItemGroupEntry(item);
        Registry.register(Registries.ITEM, new Identifier(QuarryMod.MOD_ID, name), item);
    }

    private static void addItemGroupEntry(Item item) {
        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(group -> {
            group.add(item);
        });
    }

    @SuppressWarnings("MethodCallSideOnly")
    public static void ClientInit() {
        StackToolTipHandler.setup();
        QuarryScreenRegistry.init();
    }
}
