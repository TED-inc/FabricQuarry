package net.quarrymod.init;

import net.quarrymod.block.misc.BlockDrillTube;
import net.quarrymod.blockentity.machine.tier3.QuarryBlockEntity;
import net.quarrymod.client.GuiType;

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

import techreborn.blocks.GenericMachineBlock;
import techreborn.utils.InitUtils;


public class QMContent {

	public static final Block DRILL_TUBE = new BlockDrillTube();

    public enum Machine implements ItemConvertible {
		QUARRY(new GenericMachineBlock(GuiType.QUARRY, QuarryBlockEntity::new));

        public final String name;
		public final Block block;

		<B extends Block> Machine(B block) {
			this.name = this.toString().toLowerCase(Locale.ROOT);
			this.block = block;
			InitUtils.setup(block, name);
		}

		public ItemStack getStack() {
			return new ItemStack(block);
		}

		@Override
		public Item asItem() {
			return block.asItem();
		}
    }
}
