package net.quarrymod.init;

import net.quarrymod.block.QuarryBlock;
import net.quarrymod.block.misc.BlockDrillTube;
import net.quarrymod.blockentity.machine.tier3.QuarryBlockEntity;
import net.quarrymod.items.IQuarryUpgrade;
import net.quarrymod.items.QuarryUpgradeItem;
import reborncore.api.blockentity.IUpgrade;

import java.util.Locale;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

import techreborn.utils.InitUtils;


public class QMContent {

	public static final Block DRILL_TUBE = new BlockDrillTube();

    public enum Machine implements ItemConvertible {
		QUARRY(new QuarryBlock());

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

	public enum Upgrades implements ItemConvertible {
		RANGE_EXTENDER_LVL1((quarryBlockEntity, stack) -> {
			quarryBlockEntity.rangeExtenderLevel = Math.max(quarryBlockEntity.rangeExtenderLevel, 1);
		}),
		RANGE_EXTENDER_LVL2((quarryBlockEntity, stack) -> {
			quarryBlockEntity.rangeExtenderLevel = Math.max(quarryBlockEntity.rangeExtenderLevel, 2);
		}),
		RANGE_EXTENDER_LVL3((quarryBlockEntity, stack) -> {
			quarryBlockEntity.rangeExtenderLevel = Math.max(quarryBlockEntity.rangeExtenderLevel, 3);
		}),
		FORTUNE_LVL1((quarryBlockEntity, stack) -> {
			quarryBlockEntity.fortuneLevel = Math.max(quarryBlockEntity.fortuneLevel, 1);
		}),
		FORTUNE_LVL2((quarryBlockEntity, stack) -> {
			quarryBlockEntity.fortuneLevel = Math.max(quarryBlockEntity.fortuneLevel, 2);
		}),
		FORTUNE_LVL3((quarryBlockEntity, stack) -> {
			quarryBlockEntity.fortuneLevel = Math.max(quarryBlockEntity.fortuneLevel, 3);
		}),
		SILKTOUCH((quarryBlockEntity, stack) -> {
			quarryBlockEntity.isSilkTouch |= true;
		});

		public final String name;
		public final Item item;

		Upgrades(IQuarryUpgrade upgrade) {
			name = this.toString().toLowerCase(Locale.ROOT) + "_upgrade";
			item = new QuarryUpgradeItem(name, upgrade);
			InitUtils.setup(item, name);
		}

		@Override
		public Item asItem() {
			return item;
		}
	}
}
