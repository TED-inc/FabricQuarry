package net.quarrymod.utils;

import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class VoxelShapeHelper {

    public static VoxelShape[] getRotatedHorizontalShapes(Direction from, VoxelShape source) {
        VoxelShape shapeNorth = rotateShape(from, Direction.NORTH, source);
        VoxelShape shapeEast = rotateShape(from, Direction.EAST, source);
        VoxelShape shapeSouth = rotateShape(from, Direction.SOUTH, source);
        VoxelShape shapeWest = rotateShape(from, Direction.WEST, source);
        return new VoxelShape[] {shapeSouth, shapeWest, shapeNorth, shapeEast};
    }

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[] {shape, VoxelShapes.empty()};

        int times = (to.getHorizontal() - from.getHorizontal() + 4) % 4;

        for (int i = 0; i < times; i++) {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] =
                VoxelShapes.union(buffer[1], VoxelShapes.cuboid(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }
}
