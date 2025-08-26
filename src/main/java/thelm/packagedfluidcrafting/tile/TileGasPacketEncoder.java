package thelm.packagedfluidcrafting.tile;

import com.glodblock.github.common.item.fake.FakeItemRegister;
import com.glodblock.github.integration.mek.FakeGases;

import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTank;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.IGasHandler;
import mekanism.api.gas.IGasItem;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import thelm.packagedauto.api.ISettingsCloneable;
import thelm.packagedauto.energy.EnergyStorage;
import thelm.packagedauto.tile.TileBase;
import thelm.packagedauto.tile.TileUnpackager;
import thelm.packagedfluidcrafting.client.gui.GuiGasPacketEncoder;
import thelm.packagedfluidcrafting.container.ContainerGasPacketEncoder;
import thelm.packagedfluidcrafting.inventory.InventoryGasPacketEncoder;

public class TileGasPacketEncoder extends TileBase implements ITickable, ISettingsCloneable {

	public static int energyCapacity = 5000;
	public static int energyReq = 500;
	public static int energyUsage = 100;
	public static int refreshInterval = 4;

	public boolean isWorking = false;
	public GasStack currentGas = null;
	public int requiredAmount = 100;
	public int amount = 0;
	public int remainingProgress = 0;
	public boolean powered = false;
	public boolean activated = false;

	public TileGasPacketEncoder() {
		setInventory(new InventoryGasPacketEncoder(this));
		setEnergyStorage(new EnergyStorage(this, energyCapacity));
	}

	@Override
	protected String getLocalizedName() {
		return I18n.translateToLocal("tile.packagedfluidcrafting.gas_packet_encoder.name");
	}

	@Override
	public String getConfigTypeName() {
		return "tile.packagedfluidcrafting.gas_packet_encoder.name";
	}

	@Override
	public void onLoad() {
		updatePowered();
	}

	@Override
	public void update() {
		if(!world.isRemote) {
			if(isWorking) {
				tickProcess();
				if(remainingProgress <= 0 && isTemplateValid()) {
					finishProcess();
					if(!inventory.getStackInSlot(1).isEmpty()) {
						ejectItem();
					}
					if(!canStart()) {
						endProcess();
					}
					else {
						startProcess();
					}
				}
			}
			else if(activated) {
				if(canStart()) {
					startProcess();
					tickProcess();
					activated = false;
					isWorking = true;
				}
			}
			chargeEnergy();
			if(world.getTotalWorldTime() % refreshInterval == 0) {
				if(!inventory.getStackInSlot(1).isEmpty()) {
					ejectItem();
				}
			}
		}
	}

	public boolean isTemplateValid() {
		if(currentGas == null) {
			getGas();
		}
		if(currentGas == null) {
			return false;
		}
		return true;
	}

	public boolean canStart() {
		getGas();
		if(currentGas == null) {
			return false;
		}
		if(!isTemplateValid()) {
			return false;
		}
		ItemStack slotStack = inventory.getStackInSlot(1);
		return slotStack.isEmpty();
	}

	protected boolean canFinish() {
		return remainingProgress <= 0 && isTemplateValid();
	}

	protected void getGas() {
		currentGas = null;
		ItemStack template = inventory.getStackInSlot(0);
		if(template.isEmpty()) {
			return;
		}
		GasStack gas = null;
		if(FakeGases.isGasFakeItem(template)) {
			gas = FakeItemRegister.getStack(template);
		}
		else if(template.getItem() instanceof IGasItem) {
			gas = ((IGasItem)template.getItem()).getGas(template);
		}
		if(gas != null) {
			currentGas = gas.copy();
			currentGas.amount = requiredAmount;
		}
	}

	protected void tickProcess() {
		if(amount < requiredAmount) {
			for(EnumFacing direction : EnumFacing.values()) {
				TileEntity tile = world.getTileEntity(pos.offset(direction));
				if(tile != null && tile.hasCapability(Capabilities.GAS_HANDLER_CAPABILITY, direction.getOpposite())) {
					IGasHandler gasHandler = tile.getCapability(Capabilities.GAS_HANDLER_CAPABILITY, direction.getOpposite());
					for(GasTankInfo tankInfo : gasHandler.getTankInfo()) {
						if(tankInfo instanceof GasTank) {
							GasTank tank = (GasTank)tankInfo;
							if(currentGas.isGasEqual(tank.getGas())) {
								GasStack drained = tank.draw(requiredAmount-amount, true);
								if(drained != null) {
									amount += drained.amount;
								}
							}
						}
					}
					if(currentGas.isGasEqual(gasHandler.drawGas(direction.getOpposite(), requiredAmount-amount, false))) {
						amount += gasHandler.drawGas(direction.getOpposite(), requiredAmount-amount, true).amount;
					}
				}
			}
		}
		if(amount >= requiredAmount) {
			int energy = energyStorage.extractEnergy(Math.min(energyUsage, remainingProgress), false);
			remainingProgress -= energy;
		}
	}

	protected void finishProcess() {
		if(currentGas == null) {
			getGas();
		}
		if(currentGas == null) {
			endProcess();
			return;
		}
		if(inventory.getStackInSlot(1).isEmpty()) {
			inventory.setInventorySlotContents(1, FakeGases.packGas2Packet(currentGas));
		}
		endProcess();
	}

	public void startProcess() {
		remainingProgress = energyReq;
		amount = 0;
		syncTile(false);
		markDirty();
	}

	public void endProcess() {
		remainingProgress = 0;
		amount = 0;
		isWorking = false;
		markDirty();
	}

	protected void ejectItem() {
		for(EnumFacing facing : EnumFacing.VALUES) {
			TileEntity tile = world.getTileEntity(pos.offset(facing));
			if(tile != null && !(tile instanceof TileUnpackager) && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())) {
				IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
				ItemStack stack = inventory.getStackInSlot(1);
				if(!stack.isEmpty()) {
					ItemStack stackRem = ItemHandlerHelper.insertItem(itemHandler, stack, false);
					inventory.setInventorySlotContents(1, stackRem);
				}
			}
		}
	}

	protected void chargeEnergy() {
		ItemStack energyStack = inventory.getStackInSlot(2);
		if(energyStack.hasCapability(CapabilityEnergy.ENERGY, null)) {
			int energyRequest = Math.min(energyStorage.getMaxReceive(), energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());
			energyStorage.receiveEnergy(energyStack.getCapability(CapabilityEnergy.ENERGY, null).extractEnergy(energyRequest, false), false);
			if(energyStack.getCount() <= 0) {
				inventory.setInventorySlotContents(2, ItemStack.EMPTY);
			}
		}
	}

	public void updatePowered() {
		if(world.getRedstonePowerFromNeighbors(pos) > 0 != powered) {
			powered = !powered;
			if(powered && !isWorking) {
				activated = true;
			}
			markDirty();
		}
	}

	@Override
	public int getComparatorSignal() {
		if(isWorking) {
			return 1;
		}
		if(!inventory.getStackInSlot(1).isEmpty()) {
			return 15;
		}
		return 0;
	}

	@Override
	public ISettingsCloneable.Result loadConfig(NBTTagCompound nbt, EntityPlayer player) {
		requiredAmount = nbt.getInteger("AmountReq");
		return ISettingsCloneable.Result.success();
	}

	@Override
	public ISettingsCloneable.Result saveConfig(NBTTagCompound nbt, EntityPlayer player) {
		nbt.setInteger("AmountReq", requiredAmount);
		return ISettingsCloneable.Result.success();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		isWorking = nbt.getBoolean("Working");
		amount = nbt.getInteger("Amount");
		remainingProgress = nbt.getInteger("Progress");
		powered = nbt.getBoolean("Powered");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("Working", isWorking);
		nbt.setInteger("Amount", amount);
		nbt.setInteger("Progress", remainingProgress);
		nbt.setBoolean("Powered", powered);
		return nbt;
	}

	@Override
	public void readSyncNBT(NBTTagCompound nbt) {
		super.readSyncNBT(nbt);
		requiredAmount = nbt.getInteger("AmountReq");
		currentGas = GasStack.readFromNBT(nbt.getCompoundTag("Gas"));
	}

	@Override
	public NBTTagCompound writeSyncNBT(NBTTagCompound nbt) {
		super.writeSyncNBT(nbt);
		nbt.setInteger("AmountReq", requiredAmount);
		if(currentGas != null) {
			nbt.setTag("Gas", currentGas.write(new NBTTagCompound()));
		}
		return nbt;
	}

	@Override
	public void markDirty() {
		if(isWorking && !isTemplateValid()) {
			endProcess();
		}
		super.markDirty();
	}

	public int getScaledEnergy(int scale) {
		if(energyStorage.getMaxEnergyStored() <= 0) {
			return 0;
		}
		return Math.min(scale * energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored(), scale);
	}

	public int getScaledProgress(int scale) {
		if(remainingProgress <= 0 || energyReq <= 0) {
			return 0;
		}
		return scale * (energyReq-remainingProgress) / energyReq;
	}

	@Override
	public GuiContainer getClientGuiElement(EntityPlayer player, Object... args) {
		return new GuiGasPacketEncoder(new ContainerGasPacketEncoder(player.inventory, this));
	}

	@Override
	public Container getServerGuiElement(EntityPlayer player, Object... args) {
		return new ContainerGasPacketEncoder(player.inventory, this);
	}
}
