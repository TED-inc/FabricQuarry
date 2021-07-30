package net.quarrymod;

import net.minecraft.entity.player.PlayerEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TestItem extends Item {

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.playSound(SoundEvents.BLOCK_WOOL_BREAK, 1f, 1f);
        ItemStack stack = user.getStackInHand(hand);
        stack.setCount(stack.getCount() - 1);
        return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, stack);
    }
    

    public TestItem() {
        super(new FabricItemSettings().group(ItemGroup.MISC));
    }
}
