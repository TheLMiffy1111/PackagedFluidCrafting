package thelm.packagedfluidcrafting.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import thelm.packagedauto.tile.TileDistributor;
import thelm.packagedfluidcrafting.util.MixinHooks;

@Mixin(TileDistributor.class)
public abstract class TileDistributorMixin {

	@ModifyVariable(method = "acceptPackage(Lthelm/packagedauto/api/IRecipeInfo;Ljava/util/List;Lnet/minecraft/util/EnumFacing;Z)Z", at = @At("STORE"), name = "itemHandler", remap = false)
	private IItemHandler modifyAcceptPackageItemHandler(IItemHandler original, @Local(name = "stack") ItemStack stack, @Local(name = "tile") TileEntity tile, @Local(name = "dir") EnumFacing dir) {
		return MixinHooks.INSTANCE.getConvertingItemHandler(stack, tile, dir);
	}

	@ModifyVariable(method = "distributeItems", at = @At("STORE"), name = "itemHandler", remap = false)
	private IItemHandler modifyDistributeItemHandler(IItemHandler original, @Local(name = "stack") ItemStack stack, @Local(name = "tile") TileEntity tile, @Local(name = "dir") EnumFacing dir) {
		return MixinHooks.INSTANCE.getConvertingItemHandler(stack, tile, dir);
	}
}
