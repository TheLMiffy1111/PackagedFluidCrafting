package thelm.packagedfluidcrafting.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thelm.packagedauto.network.ISelfHandleMessage;
import thelm.packagedfluidcrafting.container.ContainerFluidPacketEncoder;
import thelm.packagedfluidcrafting.container.ContainerGasPacketEncoder;

public class PacketSetFluidAmount implements ISelfHandleMessage<IMessage> {

	private int amount;

	public PacketSetFluidAmount() {}

	public PacketSetFluidAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(amount);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		amount = buf.readInt();
	}

	@Override
	public IMessage onMessage(MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().player;
		WorldServer world = player.getServerWorld();
		world.addScheduledTask(()->{
			if(player.openContainer instanceof ContainerFluidPacketEncoder) {
				ContainerFluidPacketEncoder container = (ContainerFluidPacketEncoder)player.openContainer;
				container.tile.requiredAmount = amount;
			}
			if(player.openContainer instanceof ContainerGasPacketEncoder) {
				ContainerGasPacketEncoder container = (ContainerGasPacketEncoder)player.openContainer;
				container.tile.requiredAmount = amount;
			}
		});
		return null;
	}
}
