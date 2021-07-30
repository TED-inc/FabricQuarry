package net.quarrymod;

import net.quarrymod.blocksentity.QuarryBlockEntity;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegistryManager {

    public static final Item TEST_ITEM = new TestItem(); 
	public static final Block TEST_BLOCK = new TestBlock(1, 30); 

    public static BlockEntityType<QuarryBlockEntity> PIPE_PUMP_BLOCK_ENTITY;

		
    public static void Init()
    {
        Registry.register(Registry.ITEM, new Identifier(QuarryMod.MOD_ID, "test_item"), TEST_ITEM);
		Registry.register(Registry.BLOCK, new Identifier(QuarryMod.MOD_ID, "test_block"), TEST_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(QuarryMod.MOD_ID, "test_block"), new BlockItem(TEST_BLOCK, new FabricItemSettings().group(ItemGroup.MISC)));

        PIPE_PUMP_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(QuarryMod.MOD_ID, "quarry_block"),
                BlockEntityType.Builder.create(QuarryBlockEntity::new,TEST_BLOCK).build(null));
    }

    @SuppressWarnings("MethodCallSideOnly")
    public static void ClientInit()
    {
        //BlockRenderLayerMap.INSTANCE.putBlock(TEST_BLOCK, RenderLayer.getCutout());
    }
}
