package thelm.packagedfluidcrafting.client.gui;

import com.glodblock.github.integration.mek.FakeGases;

import mekanism.api.gas.GasStack;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import thelm.packagedauto.client.gui.GuiAmountSpecifying;
import thelm.packagedauto.client.gui.GuiContainerTileBase;
import thelm.packagedauto.network.PacketHandler;
import thelm.packagedauto.network.packet.PacketSetItemStack;
import thelm.packagedfluidcrafting.container.ContainerGasAmountSpecifying;

public class GuiGasAmountSpecifying extends GuiAmountSpecifying<ContainerGasAmountSpecifying> {

	private int containerSlot;
	private GasStack stack;
	private int maxAmount;

	public GuiGasAmountSpecifying(GuiContainerTileBase<?> parent, InventoryPlayer playerInventory, int containerSlot, GasStack stack, int maxAmount) {
		super(parent, new ContainerGasAmountSpecifying(playerInventory, stack));
		this.containerSlot = containerSlot;
		this.stack = stack;
		this.maxAmount = maxAmount;
	}

	@Override
	protected int getDefaultAmount() {
		return stack.amount;
	}

	@Override
	protected int getMaxAmount() {
		return maxAmount;
	}

	@Override
	protected int[] getIncrements() {
		return new int[] {100, 500, 1000};
	}

	@Override
	protected int[] getMultipliers() {
		return new int[] {2, 3, 5};
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		fontRenderer.drawString(I18n.translateToLocal("gui.packagedfluidcrafting.gas_amount_specifying"), 7, 7, 0x404040);
		super.drawGuiContainerForegroundLayer(x, y);
	}

	@Override
	protected void onOkButtonPressed(boolean shiftDown) {
		try {
			int amount = MathHelper.clamp(Integer.parseInt(amountField.getText()), 0, maxAmount);
			GasStack newStack = stack.copy();
			newStack.amount = amount;
			PacketHandler.INSTANCE.sendToServer(new PacketSetItemStack(containerSlot, FakeGases.packGas2Packet(newStack)));
			close();
		}
		catch(NumberFormatException e) {
			// NO OP
		}
	}
}
