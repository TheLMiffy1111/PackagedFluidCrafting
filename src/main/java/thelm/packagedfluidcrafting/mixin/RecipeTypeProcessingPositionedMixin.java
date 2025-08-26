package thelm.packagedfluidcrafting.mixin;

import java.util.List;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mezz.jei.api.gui.IRecipeLayout;
import net.minecraft.item.ItemStack;
import thelm.packagedauto.recipe.RecipeTypeProcessingPositioned;
import thelm.packagedfluidcrafting.util.MixinHooks;

@Mixin(RecipeTypeProcessingPositioned.class)
public abstract class RecipeTypeProcessingPositionedMixin {

	@Inject(method = "getRecipeTransferMap", at = @At(value = "JUMP", opcode = Opcodes.GOTO, shift = At.Shift.BY, by = 2, ordinal = 2), remap = false)
	private void onGetRecipeTransferMap(IRecipeLayout recipeLayout, String category, CallbackInfoReturnable<Int2ObjectMap<ItemStack>> ci, @Local(name = "map") Int2ObjectMap<ItemStack> map, @Local(name = "index") LocalIntRef index, @Local(name = "output") List<ItemStack> output) {
		index.set(MixinHooks.INSTANCE.addToPositionedTransferMap(recipeLayout, map, index.get(), output))	;
	}
}
