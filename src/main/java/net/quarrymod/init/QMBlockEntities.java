package net.quarrymod.init;

import net.quarrymod.QuarryMod;
import net.quarrymod.blockentity.machine.tier3.QuarryBlockEntity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class QMBlockEntities {

    private static final List<BlockEntityType<?>> TYPES = new ArrayList<>();

    public static final BlockEntityType<QuarryBlockEntity> QUARRY = register(QuarryBlockEntity::new, "quarry", QMContent.Machine.QUARRY);;



    public static <T extends BlockEntity> BlockEntityType<T> register(BiFunction<BlockPos, BlockState, T> supplier, String name, ItemConvertible... items) {
		return register(supplier, name, Arrays.stream(items).map(itemConvertible -> Block.getBlockFromItem(itemConvertible.asItem())).toArray(Block[]::new));
	}

	public static <T extends BlockEntity> BlockEntityType<T> register(BiFunction<BlockPos, BlockState, T>supplier, String name, Block... blocks) {
		Validate.isTrue(blocks.length > 0, "no blocks for blockEntity entity type!");
		return register(new Identifier(QuarryMod.MOD_ID, name).toString(), FabricBlockEntityTypeBuilder.create(supplier::apply, blocks));
	}

	public static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder<T> builder) {
		BlockEntityType<T> blockEntityType = builder.build(null);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(id), blockEntityType);
		QMBlockEntities.TYPES.add(blockEntityType);
		return blockEntityType;
	}

	public static void init() {
		//Force loads the block entities at the right time
		TYPES.toString();
	}
}