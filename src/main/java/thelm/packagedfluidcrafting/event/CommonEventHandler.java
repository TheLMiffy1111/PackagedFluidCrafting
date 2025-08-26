package thelm.packagedfluidcrafting.event;

import com.glodblock.github.util.ModAndClassUtil;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thelm.packagedfluidcrafting.block.BlockFluidPacketEncoder;
import thelm.packagedfluidcrafting.block.BlockGasPacketEncoder;
import thelm.packagedfluidcrafting.config.PackagedFluidCraftingConfig;
import thelm.packagedfluidcrafting.network.PacketHandler;
import thelm.packagedfluidcrafting.tile.TileFluidPacketEncoder;
import thelm.packagedfluidcrafting.tile.TileGasPacketEncoder;

public class CommonEventHandler {

	public void registerBlock(Block block) {
		ForgeRegistries.BLOCKS.register(block);
	}

	public void registerItem(Item item) {
		ForgeRegistries.ITEMS.register(item);
	}

	public void onPreInit(FMLPreInitializationEvent event) {
		registerConfig(event);
		registerBlocks();
		registerItems();
		registerTileEntities();
		registerNetwork();
	}

	protected void registerConfig(FMLPreInitializationEvent event) {
		PackagedFluidCraftingConfig.init(event.getSuggestedConfigurationFile());
	}

	protected void registerBlocks() {
		registerBlock(BlockFluidPacketEncoder.INSTANCE);
		if(ModAndClassUtil.GAS) {
			registerBlock(BlockGasPacketEncoder.INSTANCE);
		}
	}

	protected void registerItems() {
		registerItem(BlockFluidPacketEncoder.ITEM_INSTANCE);
		if(ModAndClassUtil.GAS) {
			registerItem(BlockGasPacketEncoder.ITEM_INSTANCE);
		}
	}

	protected void registerTileEntities() {
		GameRegistry.registerTileEntity(TileFluidPacketEncoder.class, new ResourceLocation("packagedfluidcrafting:fluid_packet_encoder"));
		if(ModAndClassUtil.GAS) {
			GameRegistry.registerTileEntity(TileGasPacketEncoder.class, new ResourceLocation("packagedfluidcrafting:gas_packet_encoder"));
		}
	}

	protected void registerNetwork() {
		PacketHandler.registerPackets();
	}
}
