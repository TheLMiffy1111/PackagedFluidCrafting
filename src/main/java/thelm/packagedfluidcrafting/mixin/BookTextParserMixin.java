package thelm.packagedfluidcrafting.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.book.text.BookTextParser;

@Mixin(BookTextParser.class)
public abstract class BookTextParserMixin {

	@ModifyVariable(method = "lambda$static$14", at = @At("STORE"), name = "href", remap = false)
	private static ResourceLocation modifyParseLink(ResourceLocation original) {
		// Need to link to a book extension page
		// This issue probably should be reported to ROFL edition
		if(original.getPath().contains(":")) {
			return new ResourceLocation(original.getPath());
		}
		return original;
	}
}
