package thelm.packagedfluidcrafting.tile;

import com.glodblock.github.common.item.fake.FakeFluids;
import com.glodblock.github.common.item.fake.FakeItemRegister;

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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import thelm.packagedauto.api.ISettingsCloneable;
import thelm.packagedauto.energy.EnergyStorage;
import thelm.packagedauto.tile.TileBase;
import thelm.packagedauto.tile.TileUnpackager;
import thelm.packagedfluidcrafting.client.gui.GuiFluidPacketEncoder;
import thelm.packagedfluidcrafting.container.ContainerFluidPacketEncoder;
import thelm.packagedfluidcrafting.inventory.InventoryFluidPacketEncoder;

public class TileFluidPacketEncoder extends TileBase implements ITickable, ISettingsCloneable {

	public static int energyCapacity = 5000;
	public static int energyReq = 500;
	public static int energyUsage = 100;
	public static int refreshInterval = 4;

	public boolean isWorking = false;
	public FluidStack currentFluid = null;
	public int requiredAmount = 100;
	public int amount = 0;
	public int remainingProgress = 0;
	public boolean powered = false;
	public boolean activated = false;

	public TileFluidPacketEncoder() {
		setInventory(new InventoryFluidPacketEncoder(this));
		setEnergyStorage(new EnergyStorage(this, energyCapacity));
	}

	@Override
	protected String getLocalizedName() {
		return I18n.translateToLocal("tile.packagedfluidcrafting.fluid_packet_encoder.name");
	}

	@Override
	public String getConfigTypeName() {
		return "tile.packagedfluidcrafting.fluid_packet_encoder.name";
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
		if(currentFluid == null) {
			getFluid();
		}
		if(currentFluid == null) {
			return false;
		}
		return true;
	}

	public boolean canStart() {
		getFluid();
		if(currentFluid == null) {
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

	protected void getFluid() {
		currentFluid = null;
		ItemStack template = inventory.getStackInSlot(0);
		if(template.isEmpty()) {
			return;
		}
		FluidStack fluid = FakeFluids.isFluidFakeItem(template) ? FakeItemRegister.getStack(template) : FluidUtil.getFluidContained(template);
		if(fluid != null) {
			currentFluid = fluid.copy();
			currentFluid.amount = requiredAmount;
		}
	}

	protected void tickProcess() {
		if(amount < requiredAmount) {
			for(EnumFacing direction : EnumFacing.values()) {
				TileEntity tile = world.getTileEntity(pos.offset(direction));
				if(tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite())) {
					IFluidHandler fluidHandler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite());
					FluidStack toDrain = currentFluid.copy();
					toDrain.amount = requiredAmount-amount;
					FluidStack drained = fluidHandler.drain(toDrain, true);
					if(drained != null) {
						amount += drained.amount;
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
		if(currentFluid == null) {
			getFluid();
		}
		if(currentFluid == null) {
			endProcess();
			return;
		}
		if(inventory.getStackInSlot(1).isEmpty()) {
			inventory.setInventorySlotContents(1, FakeFluids.packFluid2Packet(currentFluid));
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
		currentFluid = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("Fluid"));
	}

	@Override
	public NBTTagCompound writeSyncNBT(NBTTagCompound nbt) {
		super.writeSyncNBT(nbt);
		nbt.setInteger("AmountReq", requiredAmount);
		if(currentFluid != null) {
			nbt.setTag("Fluid", currentFluid.writeToNBT(new NBTTagCompound()));
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

	@SideOnly(Side.CLIENT)
	@Override
	public GuiContainer getClientGuiElement(EntityPlayer player, Object... args) {
		return new GuiFluidPacketEncoder(new ContainerFluidPacketEncoder(player.inventory, this));
	}

	@Override
	public Container getServerGuiElement(EntityPlayer player, Object... args) {
		return new ContainerFluidPacketEncoder(player.inventory, this);
	}
}
