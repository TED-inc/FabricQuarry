package net.quarrymod;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class QuarryMod implements ModInitializer {

	public static final Item TEST_ITEM = new TestItem(new Item.Settings().group(ItemGroup.MISC)); 
	public static final Block TEST_BLOCK = new TestBlock(); 

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("quarrymod", "test_item"), TEST_ITEM);
		Registry.register(Registry.BLOCK, new Identifier("quarrymod", "test_block"), TEST_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("quarrymod", "test_block"), new BlockItem(TEST_BLOCK, new Item.Settings().group(ItemGroup.MISC)));
	}
}
