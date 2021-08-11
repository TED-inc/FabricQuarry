package net.quarrymod.blockentity.utils;

import java.util.List;

import net.minecraft.item.ItemStack;
import reborncore.common.blockentity.MachineBaseBlockEntity;
import reborncore.common.util.RebornInventory;
import reborncore.common.util.ItemUtils;


public final class SlotGroup<T extends MachineBaseBlockEntity> {

    private RebornInventory<T> inventory;
    private int[] slotList;

    public SlotGroup(RebornInventory<T> inventory, int[] slotList){
        this.inventory = inventory;
        this.slotList = slotList;
    }

    private boolean hasSpace(int slot, ItemStack stack) {
        ItemStack slotStack = inventory.getStack(slot);

        return slotStack.isEmpty()
            || ItemUtils.isItemEqual(slotStack, stack, true, true)
            && slotStack.getCount() + stack.getCount() <= stack.getMaxCount();
	}

	public boolean hasSpace(List<ItemStack> stacks) {
		for (ItemStack stack : stacks) {
			boolean canAdd = false;

			for (int slot : slotList) {
				if (hasSpace(slot, stack)) {
					canAdd = true;
					break;
				}
			}
			
			if (!canAdd)
				return false;
		}

		return true;
	}


	private void addStack(int slot, ItemStack stack) {
        ItemStack slotStack = inventory.getStack(slot);

		if (slotStack.isEmpty()) {
			inventory.setStack(slot, stack);
		} else if (ItemUtils.isItemEqual(slotStack, stack, true, true)) {
			slotStack.setCount((Math.min(stack.getMaxCount(), stack.getCount() + slotStack.getCount())));
		}
	}

	public void addStacks(List<ItemStack> drops){
		for (ItemStack drop : drops) {
			for (int i = 6; i < 11; i++) {
				if (hasSpace(i, drop)) {
					addStack(i, drop);
					break;
				}
			}
		}
		inventory.setChanged();
	}
}
