package thelm.packagedfluidcrafting.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import appeng.api.storage.data.IAEItemStack;
import thelm.packagedfluidcrafting.util.MixinHooks;
import thelm.packagingprovider.recipe.DirectCraftingPatternHelper;

@Mixin(DirectCraftingPatternHelper.class)
public abstract class DirectCraftingPatternHelperMixin {

	@Shadow(remap = false)
	private IAEItemStack[] inputs;
	@Shadow(remap = false)
	private IAEItemStack[] outputs;

	@Inject(method = "<init>", at = @At(value = "FIELD", target = "inputs", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER, remap = false), remap = false)
	private void onInitInputs(CallbackInfo ci) {
		MixinHooks.INSTANCE.flattenPackets(inputs);
	}

	@Inject(method = "<init>", at = @At(value = "FIELD", target = "outputs", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER, remap = false), remap = false)
	private void onInitOutputs(CallbackInfo ci) {
		MixinHooks.INSTANCE.flattenPackets(outputs);
	}
}
