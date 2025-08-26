package thelm.packagedfluidcrafting.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import thelm.packagedauto.client.gui.GuiContainerTileBase;
import thelm.packagedfluidcrafting.util.MixinHooks;

@Mixin(GuiContainerTileBase.class)
public abstract class GuiContainerTileBaseMixin extends GuiContainer {

	private GuiContainerTileBaseMixin(Container container) {
		super(container);
	}
	
	@Inject(method = "handleMouseClick", at = @At(value = "JUMP", opcode = Opcodes.IFNE, shift = At.Shift.AFTER, ordinal = 1), cancellable = true)
	private void onHandleMouseClick(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
		GuiContainerTileBase<?> parent = GuiContainerTileBase.class.cast(this);
		if(MixinHooks.INSTANCE.displayAmountSpecifyingGui(mc, parent, slot)) {
			ci.cancel();
			return;
		}
	}
}
