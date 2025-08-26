package thelm.packagedfluidcrafting.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import appeng.api.storage.data.IAEItemStack;
import thelm.packagedauto.integration.appeng.recipe.PackageCraftingPatternHelper;
import thelm.packagedfluidcrafting.util.MixinHooks;

@Mixin(PackageCraftingPatternHelper.class)
public abstract class PackageCraftingPatternHelperMixin {

	@Shadow(remap = false)
	private IAEItemStack[] inputs;

	@Inject(method = "<init>", at = @At(value = "FIELD", target = "inputs", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER, remap = false), remap = false)
	private void onInitInputs(CallbackInfo ci) {
		MixinHooks.INSTANCE.flattenPackets(inputs);
	}
}
