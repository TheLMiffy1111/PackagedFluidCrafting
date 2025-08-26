package thelm.packagedfluidcrafting.container;

import com.glodblock.github.common.item.fake.FakeFluids;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraftforge.fluids.FluidStack;
import thelm.packagedauto.container.ContainerTileBase;
import thelm.packagedauto.slot.SlotPreview;
import thelm.packagedauto.tile.TileBase;

public class ContainerFluidAmountSpecifying extends ContainerTileBase<TileBase> {

	public ContainerFluidAmountSpecifying(InventoryPlayer playerInventory, FluidStack stack) {
		super(playerInventory, null);
		InventoryBasic itemInventory = new InventoryBasic("[Null]", true, 1);
		itemInventory.setInventorySlotContents(0, FakeFluids.packFluid2Packet(stack));
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
