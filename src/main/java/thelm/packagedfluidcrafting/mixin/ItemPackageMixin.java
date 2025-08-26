package thelm.packagedfluidcrafting.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.item.ItemStack;
import thelm.packagedauto.item.ItemPackage;
import thelm.packagedfluidcrafting.util.MixinHooks;

@Mixin(ItemPackage.class)
public abstract class ItemPackageMixin {

	@ModifyArg(method = "addInformation", at = @At(value = "INVOKE", target = "Ljava/util/List;add", ordinal = 1, remap = false))
	private Object modifyOutputTooltip(Object original, @Local(name = "is") ItemStack is) {
		return MixinHooks.INSTANCE.replaceDescTooltip(is, original::toString);
	}

	@ModifyArg(method = "addInformation", at = @At(value = "INVOKE", target = "Ljava/util/List;add", ordinal = 4, remap = false))
	private Object modifyContentTooltip(Object original, @Local(name = "is") ItemStack is) {
		return MixinHooks.INSTANCE.replaceDescTooltip(is, original::toString);
	}
}
