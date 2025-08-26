package thelm.packagedfluidcrafting.mixin;

import java.util.Arrays;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import thelm.packagedauto.tile.TileUnpackager;
import thelm.packagedauto.tile.TileUnpackager.PackageTracker;
import thelm.packagedfluidcrafting.util.MixinHooks;

@Mixin(TileUnpackager.class)
public abstract class TileUnpackagerMixin {

	@Shadow(remap = false)
	private PackageTracker[] trackers;

	@ModifyVariable(method = "emptyTrackers", at = @At(value = "STORE", ordinal = 0), name = "itemHandler", remap = false)
	private IItemHandler modifyTrackerInitItemHandler(IItemHandler original, @Local(name = "tile") TileEntity tile, @Local(name = "facing") EnumFacing facing) {
		TileUnpackager.PackageTracker trackerToEmpty = Arrays.stream(trackers).filter(t->t.isFilled() && t.facing == null && t.recipe != null && !t.recipe.getRecipeType().hasMachine()).findFirst().orElse(null);
		if(trackerToEmpty == null) {
			return null;
		}
		return MixinHooks.INSTANCE.getConvertingItemHandler(trackerToEmpty.recipe.getInputs(), tile, facing.getOpposite());
	}

	@ModifyVariable(method = "emptyTrackers", at = @At(value = "STORE", ordinal = 1), name = "itemHandler", remap = false)
	private IItemHandler modifyTrackerItemHandler(IItemHandler original, @Local(name = "trackerToEmpty") PackageTracker trackerToEmpty, @Local(name = "tile") TileEntity tile, @Local(name = "facing") EnumFacing facing) {
		return MixinHooks.INSTANCE.getConvertingItemHandler(trackerToEmpty.recipe.getInputs(), tile, facing.getOpposite());
	}
}
