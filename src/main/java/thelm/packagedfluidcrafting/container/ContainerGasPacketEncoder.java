package thelm.packagedfluidcrafting.container;

import net.minecraft.entity.player.InventoryPlayer;
import thelm.packagedauto.container.ContainerTileBase;
import thelm.packagedauto.slot.SlotBase;
import thelm.packagedauto.slot.SlotRemoveOnly;
import thelm.packagedfluidcrafting.tile.TileGasPacketEncoder;

public class ContainerGasPacketEncoder extends ContainerTileBase<TileGasPacketEncoder> {

	public ContainerGasPacketEncoder(InventoryPlayer playerInventory, TileGasPacketEncoder tile) {
		super(playerInventory, tile);
		addSlotToContainer(new SlotBase(inventory, 2, 8, 53));
		addSlotToContainer(new SlotBase(inventory, 0, 44, 35));
		addSlotToContainer(new SlotRemoveOnly(inventory, 1, 134, 35));
		setupPlayerInventory();
	}
}
