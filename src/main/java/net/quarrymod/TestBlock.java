package net.quarrymod;

import net.quarrymod.blockentity.QuarryBlockEntity;
import net.quarrymod.utils.VoxelShapeHelper;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import reborncore.api.blockentity.IMachineGuiHandler;
import reborncore.common.blocks.BlockMachineBase;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import reborncore.api.ToolManager;
import reborncore.common.misc.ModSounds;

public class TestBlock extends BlockMachineBase {

    private int _energyCost;
    private int _pumpSpeedTick;
    private VoxelShape[] _blockShapes;

    public TestBlock(int energyCost, int pumpSpeedTick)
    {
        _energyCost = energyCost;
        _pumpSpeedTick = pumpSpeedTick;
        _blockShapes = VoxelShapeHelper.getRotatedHorizontalShapes(Direction.NORTH, getBaseShape());
    }

    private VoxelShape getBaseShape()
    {
        return VoxelShapes.union
        (
            VoxelShapes.cuboid(0f, 0f, 0.624f, 1.0f, 1.0f, 1.0f),
            VoxelShapes.cuboid(0.25f, 0.25f, 0.062f, 0.75f, 0.75f, 0.75f)
        );
    }

    //Now is no gui, later we need add GUI with "vacuuming progress" and upgrades
    @Override
    public IMachineGuiHandler getGui()
    {

        return null;
    }

    //Get pump item speed
    public int getEngineTickSpeed()
    {
        return _pumpSpeedTick;
    }

    //Base cost per tick
    public int getEnergyCost() { return _energyCost; }

    //Create block entity
    @Override
    public BlockEntity createBlockEntity(BlockView worldIn)
    {
        return new QuarryBlockEntity();
    }

    //make block valid view
    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx)
    {
        return _blockShapes[state.get(FACING).getHorizontal()];
    }

    //make tooltip for block
    // @Environment(EnvType.CLIENT)
    // @Override
    // public void buildTooltip    
    // {
    //     if(Screen.hasShiftDown())
    //     {
    //         tooltip.add(new TranslatableText("pipe_vacuum_pump.tooltip.vacuum_pump_block").formatted(Formatting.GOLD));
    //     }
    //     else
    //     {
    //         tooltip.add(new TranslatableText("pipe_vacuum_pump.tooltip.hold_shift").formatted(Formatting.BLUE));
    //     }

    //     tooltip.add(new TranslatableText("pipe_vacuum_pump.tooltip.speed",
    //             Formatting.GOLD, "1", getEngineTickSpeed()).formatted(Formatting.GRAY));
    //     tooltip.add(new TranslatableText("pipe_vacuum_pump.tooltip.consumption",
    //             Formatting.GOLD, getEnergyCost()).formatted(Formatting.GRAY));
    // }

    @Override
    public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn,
							  Hand hand, BlockHitResult hitResult) {

		ItemStack tool = playerIn.getStackInHand(Hand.MAIN_HAND);
		if (!tool.isEmpty() && ToolManager.INSTANCE.canHandleTool(tool)) {
			if (ToolManager.INSTANCE.handleTool(tool, pos, worldIn, playerIn, hitResult.getSide(), false)) {
				if (playerIn.isSneaking()) {
					ItemStack drop = new ItemStack(this);
					dropStack(worldIn, pos, drop);
					worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), ModSounds.BLOCK_DISMANTLE,
							SoundCategory.BLOCKS, 0.6F, 1F);
					if (!worldIn.isClient) {
						worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
					}
					return ActionResult.SUCCESS;
				}
			}
		}
		return ActionResult.PASS;
	}
}
