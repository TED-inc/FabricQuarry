package net.quarrymod;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;

public class TestBlock extends Block {

    public TestBlock() {
        super(FabricBlockSettings.of(Material.WOOL).breakByHand(false).breakByTool(FabricToolTags.HOES).strength(2f, 0.2f));
    }
}
