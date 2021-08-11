package net.quarrymod.blockentity.machine.tier3;

import net.quarrymod.block.misc.BlockDrillTube;
import net.quarrymod.blockentity.utils.SlotGroup;
import net.quarrymod.config.QMConfig;
import net.quarrymod.init.QMBlockEntities;
import net.quarrymod.init.QMContent;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.OreBlock;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import reborncore.api.IToolDrop;
import reborncore.api.blockentity.InventoryProvider;
import reborncore.client.screen.BuiltScreenHandlerProvider;
import reborncore.client.screen.builder.BuiltScreenHandler;
import reborncore.client.screen.builder.ScreenHandlerBuilder;
import reborncore.common.blocks.BlockMachineBase;
import reborncore.common.powerSystem.PowerAcceptorBlockEntity;
import reborncore.common.util.ItemUtils;
import reborncore.common.util.RebornInventory;
import team.reborn.energy.EnergySide;
import techreborn.init.TRContent;

public class QuarryBlockEntity extends PowerAcceptorBlockEntity implements IToolDrop, InventoryProvider, BuiltScreenHandlerProvider {

	public RebornInventory<QuarryBlockEntity> inventory = new RebornInventory<>(12, "QuarryBlockEntity", 64, this);
	private int digSpentedEnergy = 0;
	private SlotGroup<QuarryBlockEntity> holeFillerSlotGroup = new SlotGroup<>(inventory, new int[] { 0, 1, 2, 3 });
	private SlotGroup<QuarryBlockEntity> drillTubeSlotGroup = new SlotGroup<>(inventory, new int[] { 4, 5 });
	private SlotGroup<QuarryBlockEntity> outputSlotGroup = new SlotGroup<>(inventory, new int[] { 6, 7, 8, 9, 10 });

	public QuarryBlockEntity() {
		super(QMBlockEntities.QUARRY);
	}

	

	public boolean decreaseStoredEnergy(double aEnergy, boolean aIgnoreTooLessEnergy) {
		if (getEnergy() - aEnergy < 0 && !aIgnoreTooLessEnergy) {
			return false;
		} else {
			setEnergy(getEnergy() - aEnergy);
			if (getEnergy() < 0) {
				setEnergy(0);
				return false;
			} else {
				return true;
			}
		}
	}

	public int getValue(ItemStack itemStack) {
		if (itemStack.isItemEqualIgnoreDamage(TRContent.Parts.SCRAP.getStack())) {
			return 100;
		} else if (itemStack.getItem() == TRContent.SCRAP_BOX) {
			return 1000;
		}
		return 0;
	}

	public int getProgress() {
		return digSpentedEnergy;
	}

	public void setProgress(int progress) {
		digSpentedEnergy = progress;
	}

	public int getProgressScaled(int scale) {
		if (digSpentedEnergy != 0) {
			return Math.min(digSpentedEnergy * scale / QMConfig.quarryEnergyPerExcavation, 100);
		}
		return 0;
	}

	// TilePowerAcceptor
	@Override
	public void tick() {
		super.tick();

		if (world.isClient)
			return;

		this.charge(11);

		boolean isActive = false;

		if (digSpentedEnergy < QMConfig.quarryEnergyPerExcavation) {
			final int euNeeded = QMConfig.quarryEnergyPerExcavation / QMConfig.quarryTiksPerExcavation;
			if (getStored(EnergySide.UNKNOWN) >= euNeeded) {
				useEnergy(euNeeded);
				digSpentedEnergy += euNeeded;
				isActive = true;
			}
		}

		if (digSpentedEnergy >= QMConfig.quarryEnergyPerExcavation) {
			final boolean isMineSucessful = tryMineOre();
			if (isMineSucessful)
				digSpentedEnergy -= QMConfig.quarryEnergyPerExcavation;

			isActive = isMineSucessful;
		}

		((BlockMachineBase)world.getBlockState(pos).getBlock()).setActive(isActive, world, pos);
	}

	private boolean tryMineOre() {
		final int radius = QMConfig.quarrySqrWorkRadius;

		for (int y = 1; y < pos.getY(); y++)
			for (int x = -radius; x <= radius; x++)
				for (int z = -radius; z <= radius; z++) {
					BlockPos blockPos = pos.add(x, 0, z).down(y);
					BlockState blockState = world.getBlockState(blockPos);

					if (isOre(blockState)) {
						List<ItemStack> drop = Block.getDroppedStacks(blockState, (ServerWorld)world, pos, null);

						if (outputSlotGroup.hasSpace(drop)) {
							outputSlotGroup.addStacks(drop);
							world.removeBlock(blockPos, false);
							return true;
						}
					}
				}
		return false;
	}

	private boolean isOre(BlockState state) {
		return !state.isAir() && (state.getBlock() instanceof OreBlock || state.getBlock() instanceof RedstoneOreBlock);
	}

	@Override
	public double getBaseMaxPower() {
		return QMConfig.quarryMaxEnergy;
	}

	@Override
	public boolean canProvideEnergy(EnergySide side) {
		return false;
	}

	@Override
	public double getBaseMaxOutput() {
		return 0;
	}

	@Override
	public double getBaseMaxInput() {
		return QMConfig.quarryMaxInput;
	}

	// TileMachineBase
	@Override
	public boolean canBeUpgraded() {
		return false;
	}

	// IToolDrop
	@Override
	public ItemStack getToolDrop(PlayerEntity entityPlayer) {
		return QMContent.Machine.QUARRY.getStack();
	}

	// ItemHandlerProvider
	@Override
	public RebornInventory<QuarryBlockEntity> getInventory() {
		return inventory;
	}

	// IContainerProvider
	@Override
	public BuiltScreenHandler createScreenHandler(int syncID, PlayerEntity player) {
		return new ScreenHandlerBuilder("quarry").player(player.inventory).inventory().hotbar().addInventory()
				.blockEntity(this)
				.filterSlot(0, 30, 20, QuarryBlockEntity::holeFillerFilter)
				.filterSlot(1, 50, 20, QuarryBlockEntity::holeFillerFilter)
				.filterSlot(2, 70, 20, QuarryBlockEntity::holeFillerFilter)
				.filterSlot(3, 90, 20, QuarryBlockEntity::holeFillerFilter)
				.filterSlot(4, 120, 20, QuarryBlockEntity::drillTubeFilter)
				.filterSlot(5, 140, 20, QuarryBlockEntity::drillTubeFilter)
				.outputSlot(6, 55, 66)
				.outputSlot(7, 75, 66)
				.outputSlot(8, 95, 66)
				.outputSlot(9, 115, 66)
				.outputSlot(10, 135, 66)
				.energySlot(11, 8, 72).syncEnergyValue()
				.sync(this::getProgress, this::setProgress).addInventory().create(this, syncID);
	}

	private static boolean holeFillerFilter(ItemStack stack) {
		Item item = stack.getItem();
		if ((item instanceof BlockItem)) {
			BlockItem blockItem = (BlockItem)item;
			return blockItem.getBlock().getDefaultState().getMaterial().equals(Material.STONE);
		}
		return false;
	}

	private static boolean drillTubeFilter(ItemStack stack) {
		Item item = stack.getItem();
		if ((item instanceof BlockItem)) {
			BlockItem blockItem = (BlockItem)item;
			return blockItem.getBlock() instanceof BlockDrillTube;
		}
		return false;
	}
}
