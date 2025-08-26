package thelm.packagedfluidcrafting.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.glodblock.github.integration.pauto.PackagedFluidCrafting;

@Mixin(PackagedFluidCrafting.class)
public abstract class PackagedFluidCraftingCanceller {

	@Inject(method = "init", at = @At("HEAD"), cancellable = true, remap = false)
	private static void cancelInit(CallbackInfo ci) {
		ci.cancel();
	}
}
