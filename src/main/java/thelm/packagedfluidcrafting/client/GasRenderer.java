package thelm.packagedfluidcrafting.client;

import org.lwjgl.opengl.GL11;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

//Code from Refined Storage
public class GasRenderer {

	public static final GasRenderer INSTANCE = new GasRenderer(16, 16, 16);

	private static final int TEX_WIDTH = 16;
	private static final int TEX_HEIGHT = 16;

	private final int width;
	private final int height;
	private final int minHeight;

	public GasRenderer(int width, int height, int minHeight) {
		this.width = width;
		this.height = height;
		this.minHeight = minHeight;
	}

	private static TextureAtlasSprite getGasSprite(GasStack gasStack) {
		ResourceLocation icon = gasStack.getGas().getIcon();
		return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(icon.toString());
	}

	private static void setGLColorFromInt(int color) {
		float red = (color >> 16 & 0xFF) / 255.0F;
		float green = (color >> 8 & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;
		float alpha = ((color >> 24) & 0xFF) / 255F;
		GlStateManager.color(red, green, blue, alpha);
	}

	private static void drawTextureWithMasking(float xCoord, float yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, float zLevel) {
		float uMin = textureSprite.getMinU();
		float uMax = textureSprite.getMaxU();
		float vMin = textureSprite.getMinV();
		float vMax = textureSprite.getMaxV();
		uMax = uMax - (maskRight / 16F * (uMax - uMin));
		vMin = vMin + (maskTop / 16F * (vMax - vMin));
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bufferBuilder.pos(xCoord, yCoord + 16, zLevel).tex(uMin, vMax).endVertex();
		bufferBuilder.pos(xCoord + 16 - maskRight, yCoord + 16, zLevel).tex(uMax, vMax).endVertex();
		bufferBuilder.pos(xCoord + 16 - maskRight, yCoord + maskTop, zLevel).tex(uMax, vMin).endVertex();
		bufferBuilder.pos(xCoord, yCoord + maskTop, zLevel).tex(uMin, vMin).endVertex();
		tessellator.draw();
	}

	public void render(int xPosition, int yPosition, GasStack gasStack) {
		render(xPosition, yPosition, gasStack, Fluid.BUCKET_VOLUME);
	}

	public void render(int xPosition, int yPosition, GasStack gasStack, int capacity) {
		GlStateManager.enableBlend();
		drawGas(xPosition, yPosition, gasStack, capacity);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.disableBlend();
	}

	private void drawGas(int xPosition, int yPosition, GasStack gasStack, int capacity) {
		if(capacity <= 0 || gasStack == null) {
			return;
		}
		Gas gas = gasStack.getGas();
		TextureAtlasSprite fluidStillSprite = getGasSprite(gasStack);
		int gasColor = gas.getTint();
		int amount = gasStack.amount;
		int scaledAmount = (amount * height) / capacity;
		if(amount > 0 && scaledAmount < minHeight) {
			scaledAmount = minHeight;
		}
		if(scaledAmount > height) {
			scaledAmount = height;
		}
		drawTiledSprite(xPosition, yPosition, width, height, gasColor, scaledAmount, fluidStillSprite);
	}

	private void drawTiledSprite(int xPosition, int yPosition, int tiledWidth, int tiledHeight, int color, int scaledAmount, TextureAtlasSprite sprite) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		setGLColorFromInt(color);
		int xTileCount = tiledWidth / TEX_WIDTH;
		int xRemainder = tiledWidth - (xTileCount * TEX_WIDTH);
		int yTileCount = scaledAmount / TEX_HEIGHT;
		int yRemainder = scaledAmount - (yTileCount * TEX_HEIGHT);
		int yStart = yPosition + tiledHeight;
		for(int xTile = 0; xTile <= xTileCount; xTile++) {
			for(int yTile = 0; yTile <= yTileCount; yTile++) {
				int width = (xTile == xTileCount) ? xRemainder : TEX_WIDTH;
				int height = (yTile == yTileCount) ? yRemainder : TEX_HEIGHT;
				int x = xPosition + (xTile * TEX_WIDTH);
				int y = yStart - ((yTile + 1) * TEX_HEIGHT);
				if(width > 0 && height > 0) {
					int maskTop = TEX_HEIGHT - height;
					int maskRight = TEX_WIDTH - width;
					drawTextureWithMasking(x, y, sprite, maskTop, maskRight, 100);
				}
			}
		}
	}
}
