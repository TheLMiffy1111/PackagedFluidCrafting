package thelm.packagedfluidcrafting.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import thelm.packagedfluidcrafting.config.PackagedFluidCraftingConfig;

public class GuiPackagedFluidCraftingConfig extends GuiConfig {

	public GuiPackagedFluidCraftingConfig(GuiScreen parent) {
		super(parent, getConfigElements(), "packagedfluidcrafting", false, false, getAbridgedConfigPath(PackagedFluidCraftingConfig.config.toString()));
	}

	private static List<IConfigElement> getConfigElements() {
		ArrayList<IConfigElement> list = new ArrayList<>();
		for(String category : PackagedFluidCraftingConfig.config.getCategoryNames()) {
			list.add(new ConfigElement(PackagedFluidCraftingConfig.config.getCategory(category)));
		}
		return list;
	}
}
