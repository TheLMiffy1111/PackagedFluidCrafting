package thelm.packagedfluidcrafting.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemStack;
import thelm.packagedauto.integration.jei.EncoderGhostIngredientHandler;
import thelm.packagedfluidcrafting.util.MixinHooks;

@Mixin(EncoderGhostIngredientHandler.class)
public abstract class EncoderGhostIngredientHandlerMixin {

	@Inject(method = "wrapStack", at = @At("TAIL"), cancellable = true, remap = false)
	private static void onWrapStack(Object ingredient, CallbackInfoReturnable<ItemStack> ci) {
		ItemStack stack = MixinHooks.INSTANCE.packToPacket(ingredient);
		if(!stack.isEmpty()) {
			ci.setReturnValue(stack);
			return;
		}
	}
}
