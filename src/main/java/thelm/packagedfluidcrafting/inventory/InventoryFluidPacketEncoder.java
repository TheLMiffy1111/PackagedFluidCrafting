package thelm.packagedfluidcrafting.inventory;

import com.glodblock.github.common.item.fake.FakeFluids;

import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidUtil;
import thelm.packagedauto.inventory.InventoryTileBase;
import thelm.packagedfluidcrafting.tile.TileFluidPacketEncoder;

public class InventoryFluidPacketEncoder extends InventoryTileBase {

	public final TileFluidPacketEncoder tile;

	public InventoryFluidPacketEncoder(TileFluidPacketEncoder tile) {
		super(tile, 3);
		this.tile = tile;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		switch(index) {
		case 1: return false;
		case 2: return stack.hasCapability(CapabilityEnergy.ENERGY, null);
		default: return FakeFluids.isFluidFakeItem(stack) || FluidUtil.getFluidHandler(stack) != null; 
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
