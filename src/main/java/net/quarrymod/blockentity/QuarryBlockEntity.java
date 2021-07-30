package net.quarrymod.blockentity;

import net.quarrymod.RegistryManager;
import net.quarrymod.TestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;

import reborncore.client.screen.BuiltScreenHandlerProvider;
import reborncore.client.screen.builder.BuiltScreenHandler;
import reborncore.client.screen.builder.ScreenHandlerBuilder;
import reborncore.common.recipes.RecipeCrafter;
import reborncore.common.util.RebornInventory;
import techreborn.blockentity.machine.GenericMachineBlockEntity;
import techreborn.config.TechRebornConfig;
import techreborn.init.ModRecipes;
import techreborn.init.TRBlockEntities;
import techreborn.init.TRContent;

public class QuarryBlockEntity extends GenericMachineBlockEntity implements BuiltScreenHandlerProvider {

	public QuarryBlockEntity() {
		super(RegistryManager.QUARRY_BLOCK_ENTITY, "Quarry", 32, 1_000, RegistryManager.Machine.QUARRY.block, 2);
		final int[] inputs = new int[]{0};
		final int[] outputs = new int[]{1};
		this.inventory = new RebornInventory<>(3, "QuarryBlockEntity", 64, this);
		this.crafter = new RecipeCrafter(ModRecipes.EXTRACTOR, this, 2, 1, this.inventory, inputs, outputs);
	}

	// IContainerProvider
	@Override
	public BuiltScreenHandler createScreenHandler(int syncID, final PlayerEntity player) {
		return new ScreenHandlerBuilder("extractor").player(player.inventory).inventory().hotbar().addInventory().blockEntity(this)
				.slot(0, 55, 45).outputSlot(1, 101, 45).energySlot(2, 8, 72).syncEnergyValue().syncCrafterValue()
				.addInventory().create(this, syncID);
	}
}
