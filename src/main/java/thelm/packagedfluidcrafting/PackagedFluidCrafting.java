package thelm.packagedfluidcrafting;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.packagedfluidcrafting.block.BlockFluidPacketEncoder;
import thelm.packagedfluidcrafting.event.CommonEventHandler;

@Mod(
		modid = PackagedFluidCrafting.MOD_ID,
		name = PackagedFluidCrafting.NAME,
		version = PackagedFluidCrafting.VERSION,
		dependencies = PackagedFluidCrafting.DEPENDENCIES,
		guiFactory = PackagedFluidCrafting.GUI_FACTORY
		)
public class PackagedFluidCrafting {

	public static final String MOD_ID = "packagedfluidcrafting";
	public static final String NAME = "PackagedFluidCrafting";
	public static final String VERSION = "1.12.2-0@VERSION@";
	public static final String DEPENDENCIES = "required:mixinbooter;required-after:packagedauto@[1.12.2-1.0.22,);required-after:ae2fc@[2.5,);";
	public static final String GUI_FACTORY = "thelm.packagedfluidcrafting.client.gui.GuiPackagedFluidCraftingConfigFactory";
	public static final CreativeTabs CREATIVE_TAB = new CreativeTabs("packagedfluidcrafting") {
		@SideOnly(Side.CLIENT)
		@Override
		public ItemStack createIcon() {
			return new ItemStack(BlockFluidPacketEncoder.INSTANCE);
		}
	};
	@SidedProxy(
			clientSide = "thelm.packagedfluidcrafting.client.event.ClientEventHandler",
			serverSide = "thelm.packagedfluidcrafting.event.CommonEventHandler",
			modId = MOD_ID)
	public static CommonEventHandler eventHandler;

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event) {
		eventHandler.onPreInit(event);
	}
}
