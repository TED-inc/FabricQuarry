package net.quarrymod.client.gui;

import net.quarrymod.blockentity.machine.tier3.QuarryBlockEntity;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

import reborncore.client.gui.builder.GuiBase;
import reborncore.client.gui.guibuilder.GuiBuilder;
import reborncore.client.screen.builder.BuiltScreenHandler;

public class GuiQuarry extends GuiBase<BuiltScreenHandler> {

	QuarryBlockEntity blockEntity;

	public GuiQuarry(int syncID, final PlayerEntity player, final QuarryBlockEntity blockEntity) {
		super(player, blockEntity, blockEntity.createScreenHandler(syncID, player));
		this.blockEntity = blockEntity;
	}

	@Override
	protected void drawBackground(MatrixStack matrixStack, final float f, final int mouseX, final int mouseY) {
		super.drawBackground(matrixStack, f, mouseX, mouseY);
		final Layer layer = Layer.BACKGROUND;

		drawSlot(matrixStack, 8, 72, layer);

		drawSlot(matrixStack, 30, 20, layer);
		drawSlot(matrixStack, 50, 20, layer);
		drawSlot(matrixStack, 70, 20, layer);
		drawSlot(matrixStack, 90, 20, layer);

		drawSlot(matrixStack, 120, 20, layer);
		drawSlot(matrixStack, 140, 20, layer);
		drawOutputSlotBar(matrixStack, 54, 65, 5, layer);
	}

	@Override
	protected void drawForeground(MatrixStack matrixStack, final int mouseX, final int mouseY) {
		super.drawForeground(matrixStack, mouseX, mouseY);
		final Layer layer = Layer.FOREGROUND;

		builder.drawProgressBar(matrixStack, this, blockEntity.getProgressScaled(100), 100, 33, 62, mouseX, mouseY, GuiBuilder.ProgressDirection.UP, layer);
		builder.drawMultiEnergyBar(matrixStack, this, 9, 19, (int) blockEntity.getEnergy(), (int) blockEntity.getMaxStoredPower(), mouseX, mouseY, 0, layer);
	}
}
