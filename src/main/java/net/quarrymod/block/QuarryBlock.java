package net.quarrymod.block;

import net.quarrymod.blockentity.machine.tier3.QuarryBlockEntity;
import net.quarrymod.client.GuiType;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import techreborn.blocks.GenericMachineBlock;

public class QuarryBlock extends GenericMachineBlock {

    public static final EnumProperty<DisplayState> STATE = EnumProperty.of("state", DisplayState.class);

    public QuarryBlock() {
        super(GuiType.QUARRY, QuarryBlockEntity::new);
    }

    public void setState(DisplayState state, World world, BlockPos pos) {
		world.setBlockState(pos, world.getBlockState(pos).with(STATE, state));
    }
    
    @Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, ACTIVE, STATE);
	}

    @Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.onPlaced(world, pos, state, placer, stack);
        ((QuarryBlockEntity) world.getBlockEntity(pos)).resetOnPlaced();
	}

    public enum DisplayState implements StringIdentifiable {
		Off("off"),
		Mining("mining"),
		ExtractTube("extract_tube"),
		Error("error"),
        Complete("complete");

		private final String name;

		private DisplayState(String name) {
			this.name = name;
		}

		@Override
		public String asString() {
			return name;
		}

        public Formatting getFormating() {
            switch (this)
            {
                case ExtractTube:
                return Formatting.GREEN;
                case Complete:
                return Formatting.AQUA;
                default:
                return Formatting.RED;
            }
        }
	}
}
