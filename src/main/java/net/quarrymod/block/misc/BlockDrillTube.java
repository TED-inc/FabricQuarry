package net.quarrymod.block.misc;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;

public class BlockDrillTube extends Block {

    public BlockDrillTube() {
        super(FabricBlockSettings.of(Material.METAL, MapColor.BLACK).strength(2.0F, 3.0F).sounds(BlockSoundGroup.METAL));
    }
    
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0.31f, 0f, 0.31f, 0.69f, 1.0f, 0.69f);
    }
}
