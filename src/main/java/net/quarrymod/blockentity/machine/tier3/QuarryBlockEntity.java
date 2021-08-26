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
import net.minecraft.util.math.ChunkPos;

import reborncore.api.IToolDrop;
import reborncore.api.blockentity.InventoryProvider;
import reborncore.client.screen.BuiltScreenHandlerProvider;
import reborncore.client.screen.builder.BuiltScreenHandler;
import reborncore.client.screen.builder.ScreenHandlerBuilder;
import reborncore.common.blocks.BlockMachineBase;
import reborncore.common.powerSystem.PowerAcceptorBlockEntity;
import reborncore.common.util.RebornInventory;
import team.reborn.energy.EnergySide;
import techreborn.init.TRContent;

public class QuarryBlockEntity extends PowerAcceptorBlockEntity implements IToolDrop, InventoryProvider, BuiltScreenHandlerProvider {

	public RebornInventory<QuarryBlockEntity> inventory = new RebornInventory<>(12, "QuarryBlockEntity", 64, this);
	private double miningSpentedEnergy = 0;
	private ExcavationState excavationState = ExcavationState.InProgress;
	private BlockPos targetOrePos;

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

	public int getProgressScaled(int scale) {
		if (miningSpentedEnergy != 0) {
			return (int)Math.min(miningSpentedEnergy * scale / getEnergyPerExcavation(), 100);
		}
		return 0;
	}

	public ExcavationState getExcavationState() {
		return excavationState;
	}

	@Override
	public void tick() {
		super.tick();

		if (world.isClient)
			return;

		this.charge(11);

		if (excavationState != ExcavationState.Complete)
		{
			if (miningSpentedEnergy < getEnergyPerExcavation()) {
				final double euNeeded = getEnergyPerExcavation() / getTiksPerExcavation();
				final double euAvailable = Math.min(euNeeded, getStored(EnergySide.UNKNOWN));
				if (euAvailable > 0d) {
					useEnergy(euAvailable);
					miningSpentedEnergy += euAvailable;
					excavationState = ExcavationState.InProgress;
				}
				else
					excavationState = ExcavationState.NoEnergyIncome;
			}

			if (miningSpentedEnergy >= getEnergyPerExcavation()) {
				if (targetOrePos == null && (excavationState == ExcavationState.InProgress || excavationState == ExcavationState.NoOreInCurrentPos || excavationState == ExcavationState.CannotOutputMineDrop))
					targetOrePos = findOrePos();

				if (excavationState == ExcavationState.NoOresInCurrentDepth || excavationState == ExcavationState.NotEnoughDrillTube)
					tryDrillDownTube();
				else if (targetOrePos != null && (excavationState == ExcavationState.InProgress || excavationState == ExcavationState.CannotOutputMineDrop))
					tryMine(targetOrePos);
						
				if (excavationState == ExcavationState.InProgress)
					miningSpentedEnergy -= getEnergyPerExcavation();
			}
		}

		((BlockMachineBase)world.getBlockState(pos).getBlock()).setActive(excavationState == ExcavationState.InProgress, world, pos);
	}

	private BlockPos findOrePos() {
		final int radius = QMConfig.quarrySqrWorkRadius;

		final BlockPos upperBlockPos = pos.add(radius, 0, radius);
		final BlockPos lowerBlockPos = pos.add(-radius, 0, -radius);
		final ChunkPos upperChunkPos = world.getChunk(upperBlockPos).getPos();
		final ChunkPos lowerChunkPos = world.getChunk(lowerBlockPos).getPos();

		for (int y = getDrillTubeDepth(); y < pos.getY(); y++)
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
										excavationState = ExcavationState.InProgress;
										return blockPos;
									}
								}
							}
		excavationState = ExcavationState.NoOresInCurrentDepth;
		return null;
	}

	private void tryMine(BlockPos blockPos) {
		if (!isOre(world.getBlockState(blockPos)))
		{
			excavationState = ExcavationState.NoOreInCurrentPos;
			targetOrePos = null;
			return;
		}

		List<ItemStack> drop = Block.getDroppedStacks(world.getBlockState(blockPos), (ServerWorld)world, pos, null);

		if (outputSlotGroup.hasSpace(drop)) {
			outputSlotGroup.addStacks(drop);
			world.removeBlock(blockPos, false);
			excavationState = ExcavationState.InProgress;
		}	
		else
			excavationState = ExcavationState.CannotOutputMineDrop;
	}

	private int getDrillTubeDepth() {
		for (int y = pos.getY() - 1; y >= 0 ; y--)
			if (!isDrillTube(world.getBlockState(new BlockPos(pos.getX(), y, pos.getZ()))))
				return y + 1;
		return pos.getY();
	}

	private void tryDrillDownTube() {
		int newDepth = getDrillTubeDepth() - 1;

		if (newDepth < 0)
		{
			excavationState = ExcavationState.Complete;
			return;
		}

		BlockPos blockPos = new BlockPos(pos.getX(), newDepth, pos.getZ());
		BlockState blockState = world.getBlockState(blockPos);

		if (blockState.getHardness(null, null) < 0f)
		{
			excavationState = ExcavationState.Complete;
			return;
		}

		if (drillTubeSlotGroup.isEmpty() || !drillTubeSlotGroup.tryConsume(new ItemStack(Item.fromBlock(QMContent.DRILL_TUBE))))
		{
			excavationState = ExcavationState.NotEnoughDrillTube;
			return;
		}

		if (isOre(blockState)) {
			tryMine(blockPos);
			return;
		}

		excavationState = ExcavationState.InProgress;
		world.setBlockState(blockPos, QMContent.DRILL_TUBE.getDefaultState());
	}

	private int getTiksPerExcavation() {
		return Math.max((int) (QMConfig.quarryTiksPerExcavation * (1d - getSpeedMultiplier())), QMConfig.quarryMinTiksPerExcavation);
	}

	private double getEnergyPerExcavation() {
		return QMConfig.quarryEnergyPerExcavation * getPowerMultiplier();
	}

	private boolean isOre(BlockState state) {
		return !state.isAir() && (state.getBlock() instanceof OreBlock || state.getBlock() instanceof RedstoneOreBlock);
	}

	private boolean isDrillTube(BlockState state){
		return !state.isAir() && (state.getBlock() instanceof BlockDrillTube);
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
		return QMConfig.quarryMaxInput * (1d + getSpeedMultiplier() * QMConfig.quarryMaxInputOverclockerMultipier);
	}

	@Override
	public ItemStack getToolDrop(PlayerEntity entityPlayer) {
		return QMContent.Machine.QUARRY.getStack();
	}

	@Override
	public RebornInventory<QuarryBlockEntity> getInventory() {
		return inventory;
	}

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
				.energySlot(11, 8, 72)
				.syncEnergyValue()
				.sync(this::getProgress, this::setProgress)
				.sync(this::getState, this::setState)
				.addInventory()
				.create(this, syncID);
	}

	private double getProgress() {
		return miningSpentedEnergy;
	}

	private void setProgress(double progress) {
		miningSpentedEnergy = progress;
	}

	private int getState() {
		return excavationState.ordinal();
	}

	private void setState(int state) {
		excavationState = ExcavationState.values()[state];
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



	public enum ExcavationState {
		Complete,
		InProgress,
		NoEnergyIncome,
		NoOresInCurrentDepth,
		NoOreInCurrentPos,
		CannotOutputMineDrop,
		NotEnoughDrillTube;

		public int getColor() {

			switch (this)
			{
				case Complete:
				return 0x003090;
				default:
				return 0XB00000;
			}
		}

		public boolean isDisplayed() {
			return this != InProgress && this != NoEnergyIncome && this != NoOreInCurrentPos;
		}
	}
}
