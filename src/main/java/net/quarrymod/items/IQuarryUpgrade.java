package net.quarrymod.items;

import net.minecraft.item.ItemStack;
import net.quarrymod.blockentity.machine.tier3.QuarryBlockEntity;
import org.jetbrains.annotations.NotNull;

public interface IQuarryUpgrade {

    void process(
        @NotNull QuarryBlockEntity quarryBlockEntity,
        @NotNull ItemStack stack);
}
