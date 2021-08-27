package net.quarrymod.client.gui;

import net.quarrymod.block.QuarryBlock.DisplayState;
import net.quarrymod.blockentity.machine.tier3.QuarryBlockEntity;
import net.quarrymod.blockentity.machine.tier3.QuarryBlockEntity.ExcavationState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
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
		final Layer layer = Layer.FOREGROUND;
		
		if (withinBounds(this, mouseX, mouseY, 28, 18, 107, 37))
			builder.drawText(matrixStack, this, new TranslatableText("gui.quarrymod.quarry.filler_blocks"), 30, 40, 0xA0A0A0);
		if (withinBounds(this, mouseX, mouseY, 118, 18, 157, 37))
			builder.drawText(matrixStack, this, new TranslatableText("gui.quarrymod.quarry.drill_tubes"), 30, 40, 0xA0A0A0);

		final DisplayState displayState = blockEntity.getDisplayState();
		if (displayState != DisplayState.Off && displayState != DisplayState.Mining)
			builder.drawText(matrixStack, this, new TranslatableText("gui.quarrymod.quarry.state_" + blockEntity.getStateName().toLowerCase()), 30, 50, displayState.getColor());

		builder.drawDefaultBackground(matrixStack, this, 28, 25, 80, 6);
		
		super.drawForeground(matrixStack, mouseX, mouseY);

		builder.drawProgressBar(matrixStack, this, blockEntity.getProgressScaled(100), 100, 33, 62, mouseX, mouseY, GuiBuilder.ProgressDirection.UP, layer);
		builder.drawMultiEnergyBar(matrixStack, this, 9, 19, (int) blockEntity.getEnergy(), (int) blockEntity.getMaxStoredPower(), mouseX, mouseY, 0, layer);
	}

	private static boolean withinBounds(GuiBase<?> guiBase, int mouseX, int mouseY, int xMin, int yMin, int xMax, int yMax) {
		mouseX -= guiBase.getGuiLeft();
		mouseY -= guiBase.getGuiTop();
		return (mouseX > xMin && mouseX < xMax) && (mouseY > yMin && mouseY < yMax);
	}
}
