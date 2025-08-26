package thelm.packagedfluidcrafting.config;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import thelm.packagedfluidcrafting.tile.TileFluidPacketEncoder;
import thelm.packagedfluidcrafting.tile.TileGasPacketEncoder;

public class PackagedFluidCraftingConfig {

	private PackagedFluidCraftingConfig() {}

	public static Configuration config;

	public static void init(File file) {
		MinecraftForge.EVENT_BUS.register(PackagedFluidCraftingConfig.class);
		config = new Configuration(file);
		config.load();
		init();
	}

	public static void init() {
		String category;
		category = "blocks.fluid_packet_encoder";
		TileFluidPacketEncoder.energyCapacity = config.get(category, "energy_capacity", TileFluidPacketEncoder.energyCapacity, "How much FE the Fluid Packet Encoder should hold.", 0, Integer.MAX_VALUE).getInt();
		TileFluidPacketEncoder.energyReq = config.get(category, "energy_req", TileFluidPacketEncoder.energyReq, "How much total FE the Fluid Packet Encoder should use per operation.", 0, Integer.MAX_VALUE).getInt();
		TileFluidPacketEncoder.energyUsage = config.get(category, "energy_usage", TileFluidPacketEncoder.energyUsage, "How much FE/t maximum the Fluid Packet Encoder can use.", 0, Integer.MAX_VALUE).getInt();
		TileFluidPacketEncoder.refreshInterval = config.get(category, "refresh_interval", TileFluidPacketEncoder.refreshInterval, "How many ticks should the Fluid Packet Encoder wait between each refresh.", 1, 40).getInt();
		category = "blocks.gas_packet_encoder";
		TileGasPacketEncoder.energyCapacity = config.get(category, "energy_capacity", TileGasPacketEncoder.energyCapacity, "How much FE the Gas Packet Encoder should hold.", 0, Integer.MAX_VALUE).getInt();
		TileGasPacketEncoder.energyReq = config.get(category, "energy_req", TileGasPacketEncoder.energyReq, "How much total FE the Gas Packet Encoder should use per operation.", 0, Integer.MAX_VALUE).getInt();
		TileGasPacketEncoder.energyUsage = config.get(category, "energy_usage", TileGasPacketEncoder.energyUsage, "How much FE/t maximum the Gas Packet Encoder can use.", 0, Integer.MAX_VALUE).getInt();
		TileGasPacketEncoder.refreshInterval = config.get(category, "refresh_interval", TileGasPacketEncoder.refreshInterval, "How many ticks should the Gas Packet Encoder wait between each refresh.", 1, 40).getInt();
		if(config.hasChanged()) {
			config.save();
		}
	}
}
