package net.feltmc.neoforge.patches.mixin.client.gui.screens.inventory.tooltip;

import fr.catcore.cursedmixinextensions.annotations.Public;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TooltipRenderUtil.class)
public abstract class TooltipRenderUtilMixin {
	@Shadow
	private static void renderHorizontalLine(GuiGraphics guiGraphics, int i, int j, int k, int l, int m) {
	}
	
	@Shadow
	private static void renderVerticalLineGradient(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, int n) {
	}
	
	@Shadow
	private static void renderFrameGradient(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, int n, int o) {
	}
	
	@Shadow
	@Final
	private static int BACKGROUND_COLOR;
	
	@Shadow
	@Final
	private static int BORDER_COLOR_TOP;
	
	@Shadow
	@Final
	private static int BORDER_COLOR_BOTTOM;
	
	// Forge: Allow specifying colors for the inner border gradient and a gradient instead of a single color for the background and outer border
	@Public
	private static void renderTooltipBackground(GuiGraphics p_282666_, int p_281901_, int p_281846_, int p_281559_,
	                                           int p_283336_, int p_283422_, int backgroundTop, int backgroundBottom,
	                                           int borderTop, int borderBottom) {
		int i = p_281901_ - 3;
		int j = p_281846_ - 3;
		int k = p_281559_ + 3 + 3;
		int l = p_283336_ + 3 + 3;
		renderHorizontalLine(p_282666_, i, j - 1, k, p_283422_, backgroundTop);
		renderHorizontalLine(p_282666_, i, j + l, k, p_283422_, backgroundBottom);
		renderRectangle(p_282666_, i, j, k, l, p_283422_, backgroundTop, backgroundBottom);
		renderVerticalLineGradient(p_282666_, i - 1, j, l, p_283422_, backgroundTop, backgroundBottom);
		renderVerticalLineGradient(p_282666_, i + k, j, l, p_283422_, backgroundTop, backgroundBottom);
		renderFrameGradient(p_282666_, i, j + 1, k, l, p_283422_, borderTop, borderBottom);
	}
	
	@Inject(method = "renderTooltipBackground(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V", 
		cancellable = true, at = @At("HEAD"))
	private static void renderTooltipBackground$override(GuiGraphics p_282666_, int p_281901_, int p_281846_, int p_281559_, int p_283336_, int p_283422_, CallbackInfo ci) {
		renderTooltipBackground(p_282666_, p_281901_, p_281846_, p_281559_, p_283336_, p_283422_, BACKGROUND_COLOR, BACKGROUND_COLOR,
			BORDER_COLOR_TOP, BORDER_COLOR_BOTTOM);
		ci.cancel();
	}
	
	@Inject(method = "renderRectangle", cancellable = true, at = @At("HEAD"))
	private static void renderRectangle$override(GuiGraphics p_281392_, int p_282294_, int p_283353_, int p_282640_, int p_281964_, int p_283211_, int p_282349_, CallbackInfo ci) {
		renderRectangle(p_281392_, p_282294_, p_283353_, p_282640_, p_281964_, p_283211_, p_282349_, p_282349_);
	}
	
	// Forge: Allow specifying a gradient instead of a single color for the background
	private static void renderRectangle(GuiGraphics p_281392_, int p_282294_, int p_283353_, int p_282640_, int p_281964_, int p_283211_, int p_282349_, int colorTo) {
		p_281392_.fillGradient(p_282294_, p_283353_, p_282294_ + p_282640_, p_283353_ + p_281964_, p_283211_, p_282349_,
			colorTo);
	}
}
