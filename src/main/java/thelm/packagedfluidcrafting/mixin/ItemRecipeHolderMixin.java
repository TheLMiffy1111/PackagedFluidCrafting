package thelm.packagedfluidcrafting.mixin;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.item.ItemStack;
import thelm.packagedauto.item.ItemRecipeHolder;
import thelm.packagedfluidcrafting.util.MixinHooks;

@Mixin(ItemRecipeHolder.class)
public abstract class ItemRecipeHolderMixin {

	@ModifyArg(method = "addInformation", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;map", remap = false))
	private Function<ItemStack, String> modifyOutputTooltip(Function<ItemStack, String> original) {
		return is->MixinHooks.INSTANCE.replaceDescTooltip(is, ()->original.apply(is));
	}
}
