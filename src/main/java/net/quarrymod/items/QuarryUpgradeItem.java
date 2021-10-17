package net.quarrymod.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.quarrymod.RegistryManager;
import net.quarrymod.blockentity.machine.tier3.QuarryBlockEntity;

import org.jetbrains.annotations.NotNull;

public class QuarryUpgradeItem extends Item implements IQuarryUpgrade {

    public final String name;
	public final IQuarryUpgrade behavior;

	public QuarryUpgradeItem(String name, IQuarryUpgrade process) {
		super(RegistryManager.itemGroup.maxCount(16));
		this.name = name;
		this.behavior = process;
	}

	@Override
	public void process(
			@NotNull QuarryBlockEntity quarryBlockEntity,
			@NotNull ItemStack stack) {
		behavior.process(quarryBlockEntity, stack);
	}
}
