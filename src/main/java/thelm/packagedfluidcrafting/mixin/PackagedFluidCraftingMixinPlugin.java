package thelm.packagedfluidcrafting.mixin;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.minecraftforge.fml.common.Loader;

public class PackagedFluidCraftingMixinPlugin implements IMixinConfigPlugin {

	@Override
	public void onLoad(String mixinPackage) {}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		switch(targetClassName) {
		case "TilePackagingProviderMixin":
		case "DirectCraftingPatternHelperMixin":
			return Loader.isModLoaded("packagingprovider");
		case "RecipeTypeProcessingMixin":
		case "RecipeTypeProcessingPositionedMixin":
		case "EncoderGhostIngredientHandlerMixin":
			return Loader.isModLoaded("jei");
		case "BookTextParserMixin":
			return Loader.isModLoaded("patchouli");
		default:
			return true;
		}
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

}
