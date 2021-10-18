package net.quarrymod.items;

import org.jetbrains.annotations.NotNull;

import net.minecraft.item.ItemStack;
import net.quarrymod.blockentity.machine.tier3.QuarryBlockEntity;

public interface IQuarryUpgrade {
    void process(
        @NotNull QuarryBlockEntity quarryBlockEntity,
        @NotNull ItemStack stack);
}
