package net.quarrymod;

import net.quarrymod.client.gui.GuiQuarry;
import net.quarrymod.init.QMContent.Machine;

import java.util.Locale;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import techreborn.blocks.GenericMachineBlock;
import techreborn.client.GuiType;
import techreborn.utils.InitUtils;
import reborncore.RebornRegistry;

import java.util.Arrays;

public class RegistryManager {

    // public static final Item TEST_ITEM = new TestItem(); 
	// public static final Block TEST_BLOCK = new TestBlock(1, 30); 

    // public static BlockEntityType<QuarryBlockEntity> QUARRY_BLOCK_ENTITY;

    // public static final GuiType<QuarryBlockEntity> GUI_QUARRY = GuiType.register(new Identifier(QuarryMod.MOD_ID, "quarry"), () -> () -> GuiQuarry::new);

    

		
    public static void Init()
    {
        // Registry.register(Registry.ITEM, new Identifier(QuarryMod.MOD_ID, "test_item"), TEST_ITEM);
		// Registry.register(Registry.BLOCK, new Identifier(QuarryMod.MOD_ID, "test_block"), TEST_BLOCK);
		// Registry.register(Registry.ITEM, new Identifier(QuarryMod.MOD_ID, "test_block"), new BlockItem(TEST_BLOCK, new FabricItemSettings().group(ItemGroup.MISC)));

        // QUARRY_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(QuarryMod.MOD_ID, "quarry_block"),
        //         BlockEntityType.Builder.create(QuarryBlockEntity::new, Machine.QUARRY.block).build(null));

        // Registry.register(Registry.ITEM, new Identifier(QuarryMod.MOD_ID, "quarry_block"), new BlockItem(Machine.QUARRY.block, new FabricItemSettings().group(ItemGroup.MISC)));

		Arrays.stream(Machine.values()).forEach(value -> RebornRegistry.registerBlock(
			value.block, 
			new Item.Settings().group(ItemGroup.MISC),
			new Identifier(QuarryMod.MOD_ID, value.name)));
    }

    @SuppressWarnings("MethodCallSideOnly")
    public static void ClientInit() { }
}
