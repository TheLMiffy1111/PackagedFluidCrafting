package thelm.packagedfluidcrafting.inventory;

import com.glodblock.github.common.item.ItemFluidDrop;
import com.glodblock.github.common.item.ItemFluidPacket;
import com.glodblock.github.common.item.ItemGasDrop;
import com.glodblock.github.common.item.ItemGasPacket;
import com.glodblock.github.common.item.fake.FakeFluids;
import com.glodblock.github.common.item.fake.FakeItemRegister;
import com.glodblock.github.integration.mek.FakeGases;
import com.glodblock.github.util.ModAndClassUtil;

import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTank;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.IGasHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ItemHandlerConverting implements IItemHandler {

	private final IItemHandler itemHandler;
	private final IFluidHandler fluidHandler;
	private final Object gasHandler;
	private final EnumFacing side;

	public ItemHandlerConverting(IItemHandler itemHandler, IFluidHandler fluidHandler, Object gasHandler, EnumFacing side) {
		this.itemHandler = itemHandler;
		this.fluidHandler = fluidHandler;
		this.gasHandler = gasHandler;
		this.side = side;
	}

	public static ItemHandlerConverting wrap(ICapabilityProvider capProvider, EnumFacing side, boolean checkItem, boolean checkFluid, boolean checkGas) {
		IItemHandler itemHandler = checkItem ? capProvider.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side) : null;
		IFluidHandler fluidHandler = checkFluid ? capProvider.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side) : null;
		Object gasHandler = checkGas && ModAndClassUtil.GAS ? capProvider.getCapability(Capabilities.GAS_HANDLER_CAPABILITY, side) : null;
		if(itemHandler == null && fluidHandler == null && gasHandler == null) {
			return null;
		}
		return new ItemHandlerConverting(itemHandler, fluidHandler, gasHandler, side);
	}

	@Override
	public int getSlots() {
		int slots = 0;
		if(itemHandler != null) {
			slots += itemHandler.getSlots();
		}
		if(fluidHandler != null) {
			slots += fluidHandler.getTankProperties().length;
		}
		if(ModAndClassUtil.GAS && gasHandler != null) {
			IGasHandler gasHandler = (IGasHandler)this.gasHandler;
			slots += gasHandler.getTankInfo().length;
		}
		return slots;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if(itemHandler != null) {
			if(slot < itemHandler.getSlots()) {
				return itemHandler.getStackInSlot(slot);
			}
			slot -= itemHandler.getSlots();
		}
		if(fluidHandler != null) {
			IFluidTankProperties[] tanks = fluidHandler.getTankProperties();
			if(slot < tanks.length) {
				return FakeFluids.packFluid2Packet(tanks[slot].getContents());
			}
			slot -= tanks.length;
		}
		if(ModAndClassUtil.GAS && gasHandler != null) {
			IGasHandler gasHandler = (IGasHandler)this.gasHandler;
			GasTankInfo[] tanks = gasHandler.getTankInfo();
			if(slot < tanks.length) {
				return FakeGases.packGas2Packet(tanks[slot].getGas());
			}
			slot -= tanks.length;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if(itemHandler != null) {
			if(slot < itemHandler.getSlots()) {
				if(FakeFluids.isFluidFakeItem(stack) || ModAndClassUtil.GAS && FakeGases.isGasFakeItem(stack)) {
					return stack;
				}
				return itemHandler.insertItem(slot, stack, simulate);
			}
			slot -= itemHandler.getSlots();
		}
		if(fluidHandler != null) {
			IFluidTankProperties[] tanks = fluidHandler.getTankProperties();
			if(slot < tanks.length) {
				if(!FakeFluids.isFluidFakeItem(stack)) {
					return stack;
				}
				FluidStack toInsert = FakeItemRegister.getStack(stack);
				if(toInsert != null && toInsert.amount > 0) {
					FluidStack contained = tanks[slot].getContents();
					if(contained == null || contained.amount == 0 || contained.isFluidEqual(toInsert)) {
						if(stack.getItem() instanceof ItemFluidDrop) {
							toInsert.amount -= fluidHandler.fill(toInsert, !simulate);
							return FakeFluids.packFluid2Drops(toInsert);
						}
						else if(stack.getItem() instanceof ItemFluidPacket) {
							int insertable = fluidHandler.fill(toInsert, false);
							if(insertable >= toInsert.amount) {
								if(!simulate) {
									fluidHandler.fill(toInsert, true);
								}
								return ItemStack.EMPTY;
							}
						}
					}
				}
				return stack;
			}
			slot -= tanks.length;
		}
		if(ModAndClassUtil.GAS && gasHandler != null) {
			IGasHandler gasHandler = (IGasHandler)this.gasHandler;
			GasTankInfo[] tanks = gasHandler.getTankInfo();
			if(slot < tanks.length && FakeGases.isGasFakeItem(stack)) {
				if(!FakeGases.isGasFakeItem(stack)) {
					return stack;
				}
				GasStack toInsert = FakeItemRegister.getStack(stack);
				if(toInsert != null && toInsert.amount > 0) {
					GasStack contained = tanks[slot].getGas();
					if(contained == null || contained.amount == 0 || contained.isGasEqual(toInsert)) {
						if(stack.getItem() instanceof ItemGasDrop) {
							toInsert.amount -= gasHandler.receiveGas(side, toInsert, !simulate);
							return FakeGases.packGas2Drops(toInsert);
						}
						else if(stack.getItem() instanceof ItemGasPacket) {
							int insertable = gasHandler.receiveGas(side, toInsert, false);
							if(insertable >= toInsert.amount) {
								if(!simulate) {
									gasHandler.receiveGas(side, toInsert, true);
								}
								return ItemStack.EMPTY;
							}
						}
					}
				}
				return stack;
			}
			slot -= tanks.length;
		}
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if(itemHandler != null) {
			if(slot < itemHandler.getSlots()) {
				return itemHandler.extractItem(slot, amount, simulate);
			}
			slot -= itemHandler.getSlots();
		}
		if(fluidHandler != null) {
			IFluidTankProperties[] tanks = fluidHandler.getTankProperties();
			if(slot < tanks.length) {
				FluidStack contained = tanks[slot].getContents();
				return contained != null && contained.amount > 0 ? FakeFluids.packFluid2Drops(fluidHandler.drain(contained, !simulate)) : ItemStack.EMPTY;
			}
			slot -= tanks.length;
		}
		if(ModAndClassUtil.GAS && gasHandler != null) {
			IGasHandler gasHandler = (IGasHandler)this.gasHandler;
			GasTankInfo[] tanks = gasHandler.getTankInfo();
			if(slot < tanks.length) {
				GasStack contained = tanks[slot].getGas();
				if(contained != null && contained.amount > 0) {
					if(tanks[slot] instanceof GasTank) {
						return FakeGases.packGas2Drops(((GasTank)tanks[slot]).draw(amount, !simulate));
					}
					else {
						return FakeGases.packGas2Drops(gasHandler.drawGas(side, amount, !simulate));
					}
				}
				else {
					return ItemStack.EMPTY;
				}
			}
			slot -= tanks.length;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		if(itemHandler != null) {
			if(slot < itemHandler.getSlots()) {
				return itemHandler.getSlotLimit(slot);
			}
			slot -= itemHandler.getSlots();
		}
		if(fluidHandler != null) {
			IFluidTankProperties[] tanks = fluidHandler.getTankProperties();
			if(slot < tanks.length) {
				return tanks[slot].getCapacity();
			}
			slot -= tanks.length;
		}
		if(ModAndClassUtil.GAS && gasHandler != null) {
			IGasHandler gasHandler = (IGasHandler)this.gasHandler;
			GasTankInfo[] tanks = gasHandler.getTankInfo();
			if(slot < tanks.length) {
				return tanks[slot].getMaxGas();
			}
			slot -= tanks.length;
		}
		return 0;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		if(itemHandler != null) {
			if(slot < itemHandler.getSlots()) {
				if(FakeFluids.isFluidFakeItem(stack) || ModAndClassUtil.GAS && FakeGases.isGasFakeItem(stack)) {
					return false;
				}
				return itemHandler.isItemValid(slot, stack);
			}
			slot -= itemHandler.getSlots();
		}
		if(fluidHandler != null) {
			IFluidTankProperties[] tanks = fluidHandler.getTankProperties();
			if(slot < tanks.length) {
				return FakeFluids.isFluidFakeItem(stack);
			}
			slot -= tanks.length;
		}
		if(ModAndClassUtil.GAS && gasHandler != null) {
			IGasHandler gasHandler = (IGasHandler)this.gasHandler;
			GasTankInfo[] tanks = gasHandler.getTankInfo();
			if(slot < tanks.length) {
				return FakeGases.isGasFakeItem(stack);
			}
			slot -= tanks.length;
		}
		return false;
	}
}
