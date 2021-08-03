package net.quarrymod.blockentity.machine.tier3;

import net.quarrymod.config.QMConfig;
import net.quarrymod.init.QMBlockEntities;
import net.quarrymod.init.QMContent;

import net.minecraft.entity.player.PlayerEntity;

import reborncore.client.screen.BuiltScreenHandlerProvider;
import reborncore.client.screen.builder.BuiltScreenHandler;
import reborncore.client.screen.builder.ScreenHandlerBuilder;
import reborncore.common.recipes.RecipeCrafter;
import reborncore.common.util.RebornInventory;
import techreborn.blockentity.machine.GenericMachineBlockEntity;
import techreborn.init.ModRecipes;

public class QuarryBlockEntity extends GenericMachineBlockEntity implements BuiltScreenHandlerProvider {

	public QuarryBlockEntity() {
		super(QMBlockEntities.QUARRY, "Quarry", QMConfig.quarryMaxInput, QMConfig.quarryMaxEnergy, QMContent.Machine.QUARRY.block, 2);
		final int[] inputs = new int[]{0};
		final int[] outputs = new int[]{1};
		this.inventory = new RebornInventory<>(3, "QuarryBlockEntity", 64, this);
		this.crafter = new RecipeCrafter(ModRecipes.EXTRACTOR, this, 2, 1, this.inventory, inputs, outputs);
	}

	// IContainerProvider
	@Override
	public BuiltScreenHandler createScreenHandler(int syncID, final PlayerEntity player) {
		return new ScreenHandlerBuilder("quarry").player(player.inventory).inventory().hotbar().addInventory().blockEntity(this)
				.slot(0, 55, 45).outputSlot(1, 101, 45).energySlot(2, 8, 72).syncEnergyValue().syncCrafterValue()
				.addInventory().create(this, syncID);
	}
}
