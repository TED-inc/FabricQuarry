package net.quarrymod.blockentity.machine.tier3;

import net.quarrymod.block.QuarryBlock;
import net.quarrymod.block.QuarryBlock.DisplayState;
import net.quarrymod.block.misc.BlockDrillTube;
import net.quarrymod.blockentity.utils.SlotGroup;
import net.quarrymod.config.QMConfig;
import net.quarrymod.init.QMBlockEntities;
import net.quarrymod.init.QMContent;
import net.quarrymod.items.IQuarryUpgrade;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Material;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;

import reborncore.api.IToolDrop;
import reborncore.api.blockentity.InventoryProvider;
import reborncore.client.gui.slots.BaseSlot;
import reborncore.client.screen.BuiltScreenHandlerProvider;
import reborncore.client.screen.builder.BuiltScreenHandler;
import reborncore.client.screen.builder.ScreenHandlerBuilder;
import reborncore.common.powerSystem.PowerAcceptorBlockEntity;
import reborncore.common.recipes.RecipeCrafter;
import reborncore.common.util.RebornInventory;

import team.reborn.energy.EnergySide;

public class QuarryBlockEntity extends PowerAcceptorBlockEntity implements IToolDrop, InventoryProvider, BuiltScreenHandlerProvider {
	public int rangeExtenderLevel;
	public int fortuneLevel;
	public boolean isSilkTouch;

	public RebornInventory<QuarryBlockEntity> inventory = new RebornInventory<>(12, "QuarryBlockEntity", 64, this);
	public RebornInventory<QuarryBlockEntity> quarryUpgradesInventory = new RebornInventory<>(2, "QuarryUpgrades", 1, this);
	
	private double miningSpentedEnergy = 0;
	private ExcavationState excavationState = ExcavationState.InProgress;
	private ExcavationWorkType excavationWorkType = ExcavationWorkType.Mining;
	private boolean isMineAll = false;

	private BlockPos targetOrePos;
	private int currentTickTime = 0;

	private SlotGroup<QuarryBlockEntity> holeFillerSlotGroup = new SlotGroup<>(inventory, new int[] { 0, 1, 2, 3 });
	private SlotGroup<QuarryBlockEntity> drillTubeSlotGroup = new SlotGroup<>(inventory, new int[] { 4, 5 });
	private SlotGroup<QuarryBlockEntity> outputSlotGroup = new SlotGroup<>(inventory, new int[] { 6, 7, 8, 9, 10 });
	private SlotGroup<QuarryBlockEntity> quarryUpgradesSlotGroup = new SlotGroup<>(quarryUpgradesInventory, new int[] { 0, 1 });


	public QuarryBlockEntity() {
		super(QMBlockEntities.QUARRY);
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

	public boolean getMineAll() {
		switch (QMConfig.quarryAccessibleExcavationModes){
			case 1: // ores only
			return false;
			case 2: // all only
			return true;
			default:
			return isMineAll;
		}
	}

	public void setMineAll(boolean mineAll) {
		switch (QMConfig.quarryAccessibleExcavationModes){
			case 1: // ores only
			isMineAll = false;
			case 2: // all only
			isMineAll = true;
			default:
			isMineAll = mineAll;
		}
	}

	private void setExcavationState(ExcavationState state) {
		if (excavationState == state)
			return;

		excavationState = state;
		refreshProperty();
	}

	public ExcavationWorkType getExcavationWorkType() {
		return excavationWorkType;
	}

	private void setExcavationWorkType(ExcavationWorkType workType) {
		if (excavationWorkType == workType)
			return;

		excavationWorkType = workType;
		refreshProperty();
	}

	private void refreshProperty() {
		if (world != null)
			((QuarryBlock)world.getBlockState(pos).getBlock()).setState(getDisplayState(), world, pos);
	}

	public DisplayState getDisplayState() {
		DisplayState state = DisplayState.Off;

		if (excavationState.isError())
			state = DisplayState.Error;
		else if (excavationState == ExcavationState.InProgress)
			state = excavationWorkType == ExcavationWorkType.Mining ? 
				DisplayState.Mining : 
				DisplayState.ExtractTube;
		else if (excavationState == ExcavationState.Complete)
			state = DisplayState.Complete;

		return state;
	}

	public String getStateName() {
		if (excavationWorkType == ExcavationWorkType.ExtractTube && excavationState == ExcavationState.InProgress)
			return ExcavationWorkType.ExtractTube.toString();
		else
			return excavationState.toString();
	}

	public void resetOnPlaced() {
		setExcavationState(ExcavationState.InProgress);
		setExcavationWorkType(ExcavationWorkType.Mining);
		setProgress(0d);
	}

	@Override
	public void tick() {
		super.tick();

		if (world.isClient)
			return;

		this.charge(11);
		refreshUpgrades();
		tickQuarryLogic();

		currentTickTime++;
	}

	private void refreshUpgrades() {
		rangeExtenderLevel = 0;
		fortuneLevel = 0;
		isSilkTouch = false;

		quarryUpgradesSlotGroup.executeForAll((stack) -> {
			if (!stack.isEmpty() && stack.getItem() instanceof IQuarryUpgrade) {
				((IQuarryUpgrade)stack.getItem()).process(this, stack);
			}
		});
	}

	private void tickQuarryLogic() {
		if (excavationState != ExcavationState.Complete)
		{
			if (miningSpentedEnergy < getEnergyPerExcavation()) {
				final double euNeeded = getEnergyPerExcavation() / getTiksPerExcavation();
				final double euAvailable = Math.min(euNeeded, getStored(EnergySide.UNKNOWN));
				if (euAvailable > 0d) {
					useEnergy(euAvailable);
					miningSpentedEnergy += euAvailable;
					setExcavationState(ExcavationState.InProgress);
				}
				else
					setExcavationState(ExcavationState.NoEnergyIncome);
			}

			if (miningSpentedEnergy >= getEnergyPerExcavation()) {
				if (excavationWorkType == ExcavationWorkType.ExtractTube)
					tryDrillOutTube();
				else
				{
					if (targetOrePos == null && (excavationState == ExcavationState.InProgress || excavationState == ExcavationState.NoOreInCurrentPos || excavationState == ExcavationState.CannotOutputMineDrop))
						targetOrePos = findOrePos();

					if (excavationState == ExcavationState.NoOresInCurrentDepth || excavationState == ExcavationState.NotEnoughDrillTube)
						tryDrillDownTube();
					else if (targetOrePos != null && (excavationState == ExcavationState.InProgress || excavationState == ExcavationState.CannotOutputMineDrop))
						tryMine(targetOrePos, !getMineAll());
				}

				if (excavationState == ExcavationState.InProgress)
					miningSpentedEnergy -= getEnergyPerExcavation();
			}

			if (excavationState == ExcavationState.InProgress && currentTickTime % 20 == 0)
				RecipeCrafter.soundHandler.playSound(false, this);
		}
	}

	private BlockPos findOrePos() {
		final int radius = (int)Math.round(QMConfig.quarrySqrWorkRadiusByUpgradeLevel.get(rangeExtenderLevel));

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

									if (isOre(blockPos)) {
										setExcavationState(ExcavationState.InProgress);
										return blockPos;
									}
								}
							}
		setExcavationState(ExcavationState.NoOresInCurrentDepth);
		return null;
	}

	private void tryMine(BlockPos blockPos, boolean fillHole) {
		if (!isOre(blockPos))
		{
			setExcavationState(ExcavationState.NoOreInCurrentPos);
			targetOrePos = null;
			return;
		}

		List<ItemStack> drop = getDroppedStacks(world.getBlockState(blockPos), blockPos);

		if (outputSlotGroup.hasSpace(drop)) {
			outputSlotGroup.addStacks(drop);
			world.removeBlock(blockPos, false);
			if (fillHole)
				tryFillHole(blockPos);
			targetOrePos = null;
			setExcavationState(ExcavationState.InProgress);
		}	
		else
			setExcavationState(ExcavationState.CannotOutputMineDrop);
	}

	private void tryFillHole(BlockPos blockPos) {
		ItemStack blockToPlace = holeFillerSlotGroup.consumeAny(1, QuarryBlockEntity::holeFillerFilter);
		if (!blockToPlace.isEmpty() && blockToPlace.getItem() instanceof BlockItem)
			world.setBlockState(blockPos, ((BlockItem)blockToPlace.getItem()).getBlock().getDefaultState());
	}

	private int getDrillTubeDepth() {
		boolean hasTubes = false;

		for (int y = pos.getY() - 1; y >= 0 ; y--)
			if (!isDrillTube(world.getBlockState(new BlockPos(pos.getX(), y, pos.getZ()))))
				return y + 1;
			else
				hasTubes = true;

		return hasTubes ? 0 : pos.getY();
	}

	private void tryDrillDownTube() {
		int newDepth = getDrillTubeDepth() - 1;

		if (newDepth < 0)
		{
			setExcavationWorkType(ExcavationWorkType.ExtractTube);
			setExcavationState(ExcavationState.InProgress);
			return;
		}

		BlockPos blockPos = new BlockPos(pos.getX(), newDepth, pos.getZ());
		BlockState blockState = world.getBlockState(blockPos);
		ItemStack drillTubeItem = new ItemStack(Item.fromBlock(QMContent.DRILL_TUBE));

		if (blockState.getHardness(world, blockPos) < 0f)
		{
			setExcavationWorkType(ExcavationWorkType.ExtractTube);
			setExcavationState(ExcavationState.InProgress);
			return;
		}

		if (drillTubeSlotGroup.isEmpty() || !drillTubeSlotGroup.canConsume(drillTubeItem))
		{
			setExcavationState(ExcavationState.NotEnoughDrillTube);
			return;
		}

		if (isOre(blockPos)) {
			tryMine(blockPos, false);
			return;
		}

		if (!getMineAll()) {
			List<ItemStack> blockDrops = getDroppedStacks(blockState, blockPos);
			if (blockDrops.size() == 1) {
				ItemStack blockDrop = blockDrops.get(0);
				if (holeFillerSlotGroup.hasSpace(blockDrop) && holeFillerFilter(blockDrop)){
					holeFillerSlotGroup.addStack(blockDrop);
				}
			}
			
		}
			

		drillTubeSlotGroup.consume(drillTubeItem);
		setExcavationState(ExcavationState.InProgress);
		world.setBlockState(blockPos, QMContent.DRILL_TUBE.getDefaultState());
	}

	private void tryDrillOutTube() {
		int tubeDepth = getDrillTubeDepth();

		if (tubeDepth == pos.getY())
		{
			setExcavationState(ExcavationState.Complete);
			return;
		}

		ItemStack tubeItem = new ItemStack(Item.fromBlock(QMContent.DRILL_TUBE));

		if (drillTubeSlotGroup.hasSpace(tubeItem))
		{
			BlockPos blockPos = new BlockPos(pos.getX(), tubeDepth, pos.getZ());
			world.removeBlock(blockPos, false);
			if (!getMineAll())
				tryFillHole(blockPos);
			drillTubeSlotGroup.addStack(tubeItem);
		}
		else
			setExcavationState(ExcavationState.CannotOutputDrillTube);
	}

	private int getTiksPerExcavation() {
		return Math.max((int) (QMConfig.quarryTiksPerExcavation * (1d - getSpeedMultiplier())), QMConfig.quarryMinTiksPerExcavation);
	}

	private double getEnergyPerExcavation() {
		return QMConfig.quarryEnergyPerExcavation * getPowerMultiplier();
	}

	private boolean isOre(BlockPos blockPos) {
		BlockState state = world.getBlockState(blockPos);
		Block block = state.getBlock();

		return !state.isAir() 
		&& !(block instanceof FluidBlock)
		&& state.getHardness(world, blockPos) >= 0f
		&& !isDrillTube(state)
		&& (getMineAll() || isOreCheckId(Registry.BLOCK.getId(block).toString()));
	}

	boolean isOreCheckId(String id) {
		return id.endsWith("_ore") 
			|| QMConfig.quarryAdditioanlBlocksToMine.contains(id);
	}

	private boolean isDrillTube(BlockState state){
		return !state.isAir() && (state.getBlock() instanceof BlockDrillTube);
	}

	private List<ItemStack> getDroppedStacks(BlockState blockState, BlockPos blockPos) {
		ItemStack item = Items.NETHERITE_PICKAXE.getDefaultStack();
		item.addEnchantment(Enchantments.FORTUNE, fortuneLevel);
		item.addEnchantment(Enchantments.SILK_TOUCH, isSilkTouch ? 1 : 0);
		return Block.getDroppedStacks(blockState, (ServerWorld)world, blockPos, world.getBlockEntity(blockPos), null, item);
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
		ScreenHandlerBuilder screenHandler = new ScreenHandlerBuilder("quarry").player(player.inventory).inventory().hotbar().addInventory()
			.blockEntity(this)
			.filterSlot(0, 30, 20, QuarryBlockEntity::holeFillerFilter)
			.filterSlot(1, 48, 20, QuarryBlockEntity::holeFillerFilter)
			.filterSlot(2, 66, 20, QuarryBlockEntity::holeFillerFilter)
			.filterSlot(3, 84, 20, QuarryBlockEntity::holeFillerFilter)
			.filterSlot(4, 121, 20, QuarryBlockEntity::drillTubeFilter)
			.filterSlot(5, 139, 20, QuarryBlockEntity::drillTubeFilter)
			.outputSlot(6, 55, 66)
			.outputSlot(7, 75, 66)
			.outputSlot(8, 95, 66)
			.outputSlot(9, 115, 66)
			.outputSlot(10, 135, 66)
			.energySlot(11, 8, 72)
			.syncEnergyValue()
			.sync(this::getProgress, this::setProgress)
			.sync(this::getState, this::setState)
			.sync(this::getWorkType, this::setWorkType)
			.sync(this::getMiningAll, this::setMiningAll)
			.addInventory();

		try {
			Field slotsField = ScreenHandlerBuilder.class.getDeclaredField("slots");
			slotsField.setAccessible(true);
			List<Slot> slots = (List<Slot>)slotsField.get(screenHandler);
			slots.add(new BaseSlot(quarryUpgradesInventory, 0, -42, 30, QuarryBlockEntity::quarryUpgradesFilter));
			slots.add(new BaseSlot(quarryUpgradesInventory, 1, -42, 48, QuarryBlockEntity::quarryUpgradesFilter));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return screenHandler.create(this, syncID);
	}

	@Override
	public void fromTag(BlockState blockState, CompoundTag tag) {
		super.fromTag(blockState, tag);
		CompoundTag data = tag.getCompound("Quarry");
		setState(data.getInt("state"));
		setWorkType(data.getInt("workType"));
		setProgress(data.getDouble("progress"));
		setMiningAll(data.getInt("mineAll"));
		quarryUpgradesInventory.read(tag, "quarryUpgradesInventory");
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		CompoundTag data = new CompoundTag();
		data.putInt("state", getState());
		data.putInt("workType", getWorkType());
		data.putDouble("progress", getProgress());
		data.putInt("mineAll", getMiningAll());
		tag.put("Quarry", data);
		quarryUpgradesInventory.write(tag, "quarryUpgradesInventory");
		return tag;
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
		setExcavationState(ExcavationState.values()[state]);
	}

	private int getWorkType() {
		return excavationWorkType.ordinal();
	}

	private void setWorkType(int state) {
		excavationWorkType = ExcavationWorkType.values()[state];
	}

	private int getMiningAll() {
		return getMineAll() ? 1 : 0;
	}

	private void setMiningAll(int mineAll) {
		setMineAll(mineAll == 1);
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

	private static boolean quarryUpgradesFilter(ItemStack stack) {
		return stack.getItem() instanceof IQuarryUpgrade;
	} 

	public enum ExcavationState {
		InProgress,
		Complete,

		NoEnergyIncome,

		NoOresInCurrentDepth,
		NoOreInCurrentPos,

		CannotOutputMineDrop,
		CannotOutputDrillTube,
		NotEnoughDrillTube;

		public boolean isError() {
			return ordinal() >= CannotOutputMineDrop.ordinal();
		}
	}

	public enum ExcavationWorkType {
		Mining,
		ExtractTube,
	}
}
