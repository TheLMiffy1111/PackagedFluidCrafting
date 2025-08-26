package thelm.packagedfluidcrafting.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import thelm.packagedfluidcrafting.util.MixinHooks;
import thelm.packagingprovider.tile.TilePackagingProvider;

@Mixin(TilePackagingProvider.class)
public abstract class TilePackagingProviderMixin {

	@Shadow(remap = false)
	private List<ItemStack> toSend;
	@Shadow(remap = false)
	private EnumFacing sendDirection;

	@ModifyVariable(method = "sendUnpackaging", at = @At("STORE"), name = "itemHandler", remap = false)
	private IItemHandler modifyUnpackagingItemHandler(IItemHandler original, @Local(name = "tile") TileEntity tile) {
		return MixinHooks.INSTANCE.getConvertingItemHandler(toSend, tile, sendDirection.getOpposite());
	}

	@ModifyVariable(method = "pushPattern", at = @At("STORE"), name = "itemHandler", remap = false)
	private IItemHandler modifyPatternPushItemHandler(IItemHandler original, @Local(name = "toSend") List<ItemStack> toSend, @Local(name = "tile") TileEntity tile, @Local(name = "facing") EnumFacing facing) {
		return MixinHooks.INSTANCE.getConvertingItemHandler(toSend, tile, facing.getOpposite());
	}
}
