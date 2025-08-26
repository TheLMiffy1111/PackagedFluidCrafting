package thelm.packagedfluidcrafting.client.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.FluidStack;
import thelm.packagedauto.client.gui.GuiContainerTileBase;
import thelm.packagedfluidcrafting.client.FluidRenderer;
import thelm.packagedfluidcrafting.container.ContainerFluidPacketEncoder;
import thelm.packagedfluidcrafting.network.PacketHandler;
import thelm.packagedfluidcrafting.network.packet.PacketSetFluidAmount;

public class GuiFluidPacketEncoder extends GuiContainerTileBase<ContainerFluidPacketEncoder> {

	public static final ResourceLocation BACKGROUND = new ResourceLocation("packagedfluidcrafting:textures/gui/fluid_packet_encoder.png");
	public static final FluidRenderer FLUID_RENDERER = new FluidRenderer(16, 52, 1);

	protected GuiTextField amountField;

	public GuiFluidPacketEncoder(ContainerFluidPacketEncoder container) {
		super(container);
	}

	@Override
	protected ResourceLocation getBackgroundTexture() {
		return BACKGROUND;
	}

	@Override
	public void initGui() {
		buttonList.clear();
		super.initGui();
		amountField = new GuiTextField(0, fontRenderer, guiLeft+30, guiTop+57, 41, fontRenderer.FONT_HEIGHT);
		amountField.setEnableBackgroundDrawing(false);
		amountField.setText(String.valueOf(container.tile.requiredAmount));
		amountField.setTextColor(0xFFFFFF);
		amountField.setValidator(s->{
			if(container.tile.isWorking) {
				return false;
			}
			if(s.isEmpty()) {
				return true;
			}
			try {
				int amount = Integer.parseInt(s);
				return amount >= 1 && amount <= 1000000000;
			}
			catch(NumberFormatException e) {
				return false;
			}
		});
		amountField.setGuiResponder(new GuiPageButtonList.GuiResponder() {
			@Override
			public void setEntryValue(int id, boolean value) {}

			@Override
			public void setEntryValue(int id, float value) {}

			@Override
			public void setEntryValue(int id, String value) {
				try {
					int amount = MathHelper.clamp(Integer.parseInt(amountField.getText()), 1, 1000000000);
					if(amount != container.tile.requiredAmount) {
						PacketHandler.INSTANCE.sendToServer(new PacketSetFluidAmount(amount));
					}
				}
				catch(NumberFormatException e) {
					// NO OP
				}
			}
		});
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		drawTexturedModalRect(guiLeft+102, guiTop+35, 176, 0, container.tile.getScaledProgress(22), 16);
		int scaledEnergy = container.tile.getScaledEnergy(40);
		drawTexturedModalRect(guiLeft+10, guiTop+10+40-scaledEnergy, 176, 16+40-scaledEnergy, 12, scaledEnergy);
		if(container.tile.isWorking) {
			drawTexturedModalRect(guiLeft+102, guiTop+30, 176, 61, 6, 5);
		}
		else {
			drawTexturedModalRect(guiLeft+102, guiTop+30, 176, 56, 6, 5);
		}
		amountField.drawTextBox();

		if(container.tile.currentFluid != null) {
			FluidStack stack = container.tile.currentFluid.copy();
			stack.amount = container.tile.amount;
			FLUID_RENDERER.render(guiLeft+80, guiTop+17, stack, container.tile.requiredAmount);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String s = container.inventory.getDisplayName().getUnformattedText();
		fontRenderer.drawString(s, xSize/2 - fontRenderer.getStringWidth(s)/2, 6, 0x404040);
		fontRenderer.drawString(container.playerInventory.getDisplayName().getUnformattedText(), container.getPlayerInvX(), container.getPlayerInvY()-11, 0x404040);
		if(mouseX-guiLeft >= 10 && mouseY-guiTop >= 10 && mouseX-guiLeft <= 21 && mouseY-guiTop <= 49) {
			drawHoveringText(container.tile.getEnergyStorage().getEnergyStored()+" / "+container.tile.getEnergyStorage().getMaxEnergyStored()+" FE", mouseX-guiLeft, mouseY-guiTop);
		}
		if(!container.tile.isWorking && mouseX-guiLeft >= 102 && mouseY-guiTop >= 30 && mouseX-guiLeft <= 107 && mouseY-guiTop <= 34) {
			drawHoveringText(I18n.translateToLocal("tile.packagedfluidcrafting.fluid_packet_encoder.redstone"), mouseX-guiLeft, mouseY-guiTop);
		}
		if(container.tile.isWorking && container.tile.currentFluid != null && mouseX-guiLeft >= 80 && mouseY-guiTop >= 17 && mouseX-guiLeft <= 95 && mouseY-guiTop <= 68) {
			drawHoveringText(container.tile.currentFluid.getLocalizedName()+" "+container.tile.amount+" / "+container.tile.requiredAmount+" mB", mouseX-guiLeft, mouseY-guiTop);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		amountField.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(amountField.textboxKeyTyped(typedChar, keyCode)) {
			return;
		}
		if(mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode) && amountField.isFocused()) {
			return;
		}
		super.keyTyped(typedChar, keyCode);
	}
}
