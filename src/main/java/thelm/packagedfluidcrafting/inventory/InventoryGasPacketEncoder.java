package thelm.packagedfluidcrafting.inventory;

import com.glodblock.github.integration.mek.FakeGases;

import mekanism.api.gas.IGasItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import thelm.packagedauto.inventory.InventoryTileBase;
import thelm.packagedfluidcrafting.tile.TileGasPacketEncoder;

public class InventoryGasPacketEncoder extends InventoryTileBase {

	public final TileGasPacketEncoder tile;

	public InventoryGasPacketEncoder(TileGasPacketEncoder tile) {
		super(tile, 3);
		this.tile = tile;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		switch(index) {
		case 1: return false;
		case 2: return stack.hasCapability(CapabilityEnergy.ENERGY, null);
		default: return FakeGases.isGasFakeItem(stack) || stack.getItem() instanceof IGasItem;
		}
	}

	@Override
	public int getField(int id) {
		switch(id) {
		case 0: return tile.requiredAmount;
		case 1: return tile.amount;
		case 2: return tile.remainingProgress;
		case 3: return tile.isWorking ? 1 : 0;
		case 4: return tile.getEnergyStorage().getEnergyStored();
		default: return 0;
		}
	}

	@Override
	public void setField(int id, int value) {
		switch(id) {
		case 0:
			tile.requiredAmount = value;
			break;
		case 1:
			tile.amount = value;
			break;
		case 2:
			tile.remainingProgress = value;
			break;
		case 3:
			tile.isWorking = value != 0;
			break;
		case 4:
			tile.getEnergyStorage().setEnergyStored(value);
			break;
		}
	}

	@Override
	public int getFieldCount() {
		return 5;
	}
}
