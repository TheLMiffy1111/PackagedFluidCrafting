package thelm.packagedfluidcrafting.container;

import com.glodblock.github.integration.mek.FakeGases;

import mekanism.api.gas.GasStack;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.InventoryBasic;
import thelm.packagedauto.container.ContainerTileBase;
import thelm.packagedauto.slot.SlotPreview;
import thelm.packagedauto.tile.TileBase;

public class ContainerGasAmountSpecifying extends ContainerTileBase<TileBase> {

	public ContainerGasAmountSpecifying(InventoryPlayer playerInventory, GasStack stack) {
		super(playerInventory, null);
		InventoryBasic itemInventory = new InventoryBasic("[Null]", true, 1);
		itemInventory.setInventorySlotContents(0, FakeGases.packGas2Packet(stack));
		addSlotToContainer(new SlotPreview(itemInventory, 0, 89, 48));
	}

	@Override
	public int getPlayerInvX() {
		return 0;
	}

	@Override
	public int getPlayerInvY() {
		return 0;
	}

	@Override
	public int getSizeInventory() {
		return 0;
	}
}
