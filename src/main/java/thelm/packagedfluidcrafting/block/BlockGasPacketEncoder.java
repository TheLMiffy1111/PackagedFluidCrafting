package thelm.packagedfluidcrafting.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.packagedauto.block.BlockBase;
import thelm.packagedauto.tile.TileBase;
import thelm.packagedfluidcrafting.PackagedFluidCrafting;
import thelm.packagedfluidcrafting.tile.TileGasPacketEncoder;

public class BlockGasPacketEncoder extends BlockBase {

	public static final BlockGasPacketEncoder INSTANCE = new BlockGasPacketEncoder();
	public static final Item ITEM_INSTANCE = new ItemBlock(INSTANCE).setRegistryName("packagedfluidcrafting:gas_packet_encoder");
	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation("packagedfluidcrafting:gas_packet_encoder#normal");

	public BlockGasPacketEncoder() {
		super(Material.IRON);
		setHardness(10F);
		setResistance(25F);
		setSoundType(SoundType.METAL);
		setTranslationKey("packagedfluidcrafting.gas_packet_encoder");
		setRegistryName("packagedfluidcrafting:gas_packet_encoder");
		setCreativeTab(PackagedFluidCrafting.CREATIVE_TAB);
	}

	@Override
	public TileBase createNewTileEntity(World worldIn, int meta) {
		return new TileGasPacketEncoder();
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if(tileentity instanceof TileGasPacketEncoder) {
			((TileGasPacketEncoder)tileentity).updatePowered();
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(ITEM_INSTANCE, 0, MODEL_LOCATION);
	}
}
