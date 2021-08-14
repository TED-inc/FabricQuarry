package net.quarrymod.blockentity.machine.tier3;

import net.quarrymod.config.QMConfig;
import net.quarrymod.init.QMBlockEntities;
import net.quarrymod.init.QMContent;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

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
	private boolean exacavationComplete = false;

	public QuarryBlockEntity() {
		super(QMBlockEntities.QUARRY);
	}


	private boolean spaceForOutput(int slot, ItemStack stack) {
		return inventory.getStack(slot).isEmpty()
				|| ItemUtils.isItemEqual(inventory.getStack(slot), stack, true, true)
				&& inventory.getStack(slot).getCount() + stack.getCount() <= stack.getMaxCount();
	}

	private boolean spaceForOutput(List<ItemStack> outputs) {
		for (ItemStack drop : outputs) {
			boolean canAdd = false;

			for (int i = 6; i < 11; i++) {
				if (spaceForOutput(i, drop)) {
					canAdd = true;
					break;
				}
			}
			
			if (!canAdd)
				return false;
		}

		return true;
	}


	private void addOutputProducts(int slot, ItemStack stack) {
		if (inventory.getStack(slot).isEmpty()) {
			inventory.setStack(slot, stack);
		} else if (ItemUtils.isItemEqual(this.inventory.getStack(slot), stack, true, true)) {
			inventory.getStack(slot).setCount((Math.min(stack.getMaxCount(), stack.getCount() + inventory.getStack(slot).getCount())));
		}
	}

	private void addOutputProducts(List<ItemStack> drops){
		for (ItemStack drop : drops) {
			for (int i = 6; i < 11; i++) {
				if (spaceForOutput(i, drop)) {
					addOutputProducts(i, drop);
					break;
				}
			}
		}
		inventory.setChanged();
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

		if (!exacavationComplete && digSpentedEnergy >= QMConfig.quarryEnergyPerExcavation) {
			final boolean isMineSucessful = tryMineOre();
			if (isMineSucessful)
				digSpentedEnergy -= QMConfig.quarryEnergyPerExcavation;

			isActive = isMineSucessful;
		}

		((BlockMachineBase)world.getBlockState(pos).getBlock()).setActive(isActive, world, pos);
	}

	private boolean tryMineOre() {
		final int radius = QMConfig.quarrySqrWorkRadius * 100;
		exacavationComplete = true;

		final BlockPos upperBlockPos = pos.add(radius, 0, radius);
		final BlockPos lowerBlockPos = pos.add(-radius, 0, -radius);
		final ChunkPos upperChunkPos = world.getChunk(upperBlockPos).getPos();
		final ChunkPos lowerChunkPos = world.getChunk(lowerBlockPos).getPos();

		for (int y = pos.getY(); y >= 0 ; y--)
			for (int chunkX = lowerChunkPos.x; chunkX <= upperChunkPos.x; chunkX++)
				for (int chunkZ = lowerChunkPos.z; chunkZ <= upperChunkPos.z; chunkZ++)
					if (world.isChunkLoaded(chunkX, chunkZ))
						for (int x = 0; x < 16; x++)
							for (int z = 0; z < 16; z++) {
								int blockX = chunkX * 16 + x;
								int blockZ = chunkZ * 16 + z;

								if (blockX <= upperBlockPos.getX() && 
									blockZ <= upperBlockPos.getZ() &&
									blockX >= lowerBlockPos.getX() &&
									blockZ >= lowerBlockPos.getZ()) 
								{
									BlockPos blockPos = new BlockPos(blockX, y, blockZ);
									BlockState blockState = world.getBlockState(blockPos);

									if (isOre(blockState)) {
										List<ItemStack> drop = Block.getDroppedStacks(blockState, (ServerWorld)world, pos, null);
										exacavationComplete = false;

										if (spaceForOutput(drop)) {
											addOutputProducts(drop);
											world.removeBlock(blockPos, false);
											return true;
										}
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
				.blockEntity(this).slot(0, 30, 20).slot(1, 50, 20).slot(2, 70, 20).slot(3, 90, 20).slot(4, 110, 20)
				.slot(5, 130, 20).outputSlot(6, 40, 66).outputSlot(7, 60, 66).outputSlot(8, 80, 66)
				.outputSlot(9, 100, 66).outputSlot(10, 120, 66).energySlot(11, 8, 72).syncEnergyValue()
				.sync(this::getProgress, this::setProgress).addInventory().create(this, syncID);
	}
}