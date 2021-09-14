package net.quarrymod.packets;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.quarrymod.QuarryMod;
import net.quarrymod.blockentity.machine.tier3.QuarryBlockEntity;
import reborncore.common.network.IdentifiedPacket;
import reborncore.common.network.NetworkManager;

public class QMServerboundPackets {

	public static final Identifier QUARRY_MINE_ALL = new Identifier(QuarryMod.MOD_ID, "quarry_mine_all");

	public static void init() {
		NetworkManager.registerServerBoundHandler(QUARRY_MINE_ALL, (server, player, handler, buf, responseSender) -> {
			BlockPos machinePos = buf.readBlockPos();
			boolean mineAll = buf.readBoolean();

			server.execute(() -> {
				BlockEntity BlockEntity = player.world.getBlockEntity(machinePos);
				if (BlockEntity instanceof QuarryBlockEntity) {
					((QuarryBlockEntity) BlockEntity).setMineAll(mineAll);
				}
			});
		});
	}

	public static IdentifiedPacket createPacketQuarryMineAll(QuarryBlockEntity machine, boolean mineAll) {
		return NetworkManager.createServerBoundPacket(QUARRY_MINE_ALL, buf -> {
			buf.writeBlockPos(machine.getPos());
			buf.writeBoolean(mineAll);
		});
	}
}
