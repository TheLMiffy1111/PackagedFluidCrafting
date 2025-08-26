package thelm.packagedfluidcrafting.mixin;

import java.util.Collections;
import java.util.List;

import zone.rong.mixinbooter.ILateMixinLoader;

public class PackagedFluidCraftingMixinLoader implements ILateMixinLoader {

	@Override
	public List<String> getMixinConfigs() {
		return Collections.singletonList("packagedfluidcrafting.mixins.json");
	}
}
