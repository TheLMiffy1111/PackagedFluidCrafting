package thelm.packagedfluidcrafting.util;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.glodblock.github.common.item.ItemFluidPacket;
import com.glodblock.github.common.item.ItemGasPacket;
import com.glodblock.github.common.item.fake.FakeFluids;
import com.glodblock.github.common.item.fake.FakeItemRegister;
import com.glodblock.github.integration.mek.FakeGases;
import com.glodblock.github.util.ModAndClassUtil;

import appeng.api.storage.data.IAEItemStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mekanism.api.gas.GasStack;
import mekanism.client.jei.MekanismJEI;
import mekanism.common.capabilities.Capabilities;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import thelm.packagedauto.client.gui.GuiContainerTileBase;
import thelm.packagedfluidcrafting.client.gui.GuiFluidAmountSpecifying;
import thelm.packagedfluidcrafting.client.gui.GuiGasAmountSpecifying;
import thelm.packagedfluidcrafting.inventory.ItemHandlerConverting;

public class MixinHooks {

	public static final MixinHooks INSTANCE = new MixinHooks();

	private MixinHooks() {}

	public String replaceDescTooltip(ItemStack itemStack, Supplier<String> original) {
		if(FakeFluids.isFluidFakeItem(itemStack)) {
			FluidStack fluidStack = FakeItemRegister.getStack(itemStack);
			if(fluidStack != null) {
				return fluidStack.amount+"mB "+fluidStack.getLocalizedName();
			}
		}
		else if(ModAndClassUtil.GAS && FakeGases.isGasFakeItem(itemStack)) {
			GasStack gasStack = FakeItemRegister.getStack(itemStack);
			if(gasStack != null) {
				return gasStack.amount+"mB "+gasStack.getGas().getLocalizedName();
			}
		}
		return original.get();
	}

	public ItemStack packToPacket(Object ingredient) {
		if(ingredient instanceof FluidStack) {
			return FakeFluids.packFluid2Packet((FluidStack)ingredient);
		}
		else if(ModAndClassUtil.GAS && ingredient instanceof GasStack) {
			return FakeGases.packGas2Packet((GasStack)ingredient);
		}
		return ItemStack.EMPTY;
	}

	public boolean hasGasHandler(ICapabilityProvider capProvider, EnumFacing side) {
		return ModAndClassUtil.GAS && capProvider.hasCapability(Capabilities.GAS_HANDLER_CAPABILITY, side);
	}

	public IItemHandler getConvertingItemHandler(List<ItemStack> stacks, TileEntity tile, EnumFacing facing) {
		boolean checkItem = !stacks.stream().allMatch(stack->FakeFluids.isFluidFakeItem(stack) || ModAndClassUtil.GAS && FakeGases.isGasFakeItem(stack));
		boolean checkFluid = stacks.stream().anyMatch(FakeFluids::isFluidFakeItem);
		boolean checkGas = ModAndClassUtil.GAS && stacks.stream().anyMatch(FakeGases::isGasFakeItem);
		return ItemHandlerConverting.wrap(tile, facing, checkItem, checkFluid, checkGas);
	}

	public IItemHandler getConvertingItemHandler(ItemStack stack, TileEntity tile, EnumFacing facing) {
		boolean checkItem = !(FakeFluids.isFluidFakeItem(stack) || ModAndClassUtil.GAS && FakeGases.isGasFakeItem(stack));
		boolean checkFluid = FakeFluids.isFluidFakeItem(stack);
		boolean checkGas = ModAndClassUtil.GAS && FakeGases.isGasFakeItem(stack);
		return ItemHandlerConverting.wrap(tile, facing, checkItem, checkFluid, checkGas);
	}

	public void flattenPackets(IAEItemStack[] stacks) {
		for(int i = 0; i < stacks.length; ++i) {
			if(stacks[i] != null && stacks[i].getItem() instanceof ItemFluidPacket) {
				stacks[i] = FakeFluids.packFluid2AEDrops((FluidStack)FakeItemRegister.getStack(stacks[i]));
			}
		}
		if(ModAndClassUtil.GAS) {
			for(int i = 0; i < stacks.length; ++i) {
				if(stacks[i] != null && stacks[i].getItem() instanceof ItemGasPacket) {
					stacks[i] = FakeGases.packGas2AEDrops((GasStack)FakeItemRegister.getStack(stacks[i]));
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean displayAmountSpecifyingGui(Minecraft mc, GuiContainerTileBase<?> parent, Slot slot) {
		ItemStack is = slot.getStack();
		if(FakeFluids.isFluidFakeItem(is)) {
			FluidStack fs = FakeItemRegister.getStack(is);
			if(fs != null) {
				mc.displayGuiScreen(new GuiFluidAmountSpecifying(parent, mc.player.inventory, slot.slotNumber, fs, 1000000000));
				return true;
			}
		}
		else if(ModAndClassUtil.GAS && FakeGases.isGasFakeItem(is)) {
			GasStack gs = FakeItemRegister.getStack(is);
			if(gs != null) {
				mc.displayGuiScreen(new GuiGasAmountSpecifying(parent, mc.player.inventory, slot.slotNumber, gs, 1000000000));
				return true;
			}
		}
		return false;
	}

	@Optional.Method(modid = "jei")
	public void addToProcessingTransferMap(IRecipeLayout recipeLayout, List<ItemStack> input, List<ItemStack> output) {
		Map<Integer, ? extends IGuiIngredient<FluidStack>> fluidIngredients = recipeLayout.getFluidStacks().getGuiIngredients();
		for(Map.Entry<Integer, ? extends IGuiIngredient<FluidStack>> entry : fluidIngredients.entrySet()) {
			IGuiIngredient<FluidStack> ingredient = entry.getValue();
			FluidStack displayed = entry.getValue().getDisplayedIngredient();
			if(displayed != null) {
				if(ingredient.isInput()) {
					input.add(FakeFluids.packFluid2Packet(displayed));
				}
				else {
					output.add(FakeFluids.packFluid2Packet(displayed));
				}
			}
		}
		if(ModAndClassUtil.GAS) {
			Map<Integer, ? extends IGuiIngredient<GasStack>> gasIngredients = recipeLayout.getIngredientsGroup(MekanismJEI.TYPE_GAS).getGuiIngredients();
			for(Map.Entry<Integer, ? extends IGuiIngredient<GasStack>> entry : gasIngredients.entrySet()) {
				IGuiIngredient<GasStack> ingredient = entry.getValue();
				GasStack displayed = entry.getValue().getDisplayedIngredient();
				if(displayed != null) {
					if(ingredient.isInput()) {
						input.add(FakeGases.packGas2Packet(displayed));
					}
					else {
						output.add(FakeGases.packGas2Packet(displayed));
					}
				}
			}
		}
	}

	@Optional.Method(modid = "jei")
	public int addToPositionedTransferMap(IRecipeLayout recipeLayout, Int2ObjectMap<ItemStack> map, int index, List<ItemStack> output) {
		Map<Integer, ? extends IGuiIngredient<FluidStack>> fluidIngredients = recipeLayout.getFluidStacks().getGuiIngredients();
		for(Map.Entry<Integer, ? extends IGuiIngredient<FluidStack>> entry : fluidIngredients.entrySet()) {
			IGuiIngredient<FluidStack> ingredient = entry.getValue();
			if(ingredient.isInput() && index >= 81) {
				continue;
			}
			FluidStack displayed = entry.getValue().getDisplayedIngredient();
			if(displayed != null) {
				if(ingredient.isInput()) {
					map.put(index, FakeFluids.packFluid2Packet(displayed));
				}
				else {
					output.add(FakeFluids.packFluid2Packet(displayed));
				}
			}
			if(ingredient.isInput()) {
				index++;
			}
		}
		if(ModAndClassUtil.GAS) {
			Map<Integer, ? extends IGuiIngredient<GasStack>> gasIngredients = recipeLayout.getIngredientsGroup(MekanismJEI.TYPE_GAS).getGuiIngredients();
			for(Map.Entry<Integer, ? extends IGuiIngredient<GasStack>> entry : gasIngredients.entrySet()) {
				IGuiIngredient<GasStack> ingredient = entry.getValue();
				if(ingredient.isInput() && index >= 81) {
					continue;
				}
				GasStack displayed = entry.getValue().getDisplayedIngredient();
				if(displayed != null) {
					if(ingredient.isInput()) {
						map.put(index, FakeGases.packGas2Packet(displayed));
					}
					else {
						output.add(FakeGases.packGas2Packet(displayed));
					}
				}
				if(ingredient.isInput()) {
					index++;
				}
			}
		}
		return index;
	}
}
