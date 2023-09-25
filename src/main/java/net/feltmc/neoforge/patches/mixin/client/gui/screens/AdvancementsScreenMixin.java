package net.feltmc.neoforge.patches.mixin.client.gui.screens;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fr.catcore.cursedmixinextensions.annotations.ShadowSuper;
import net.feltmc.feltasm.asm.CreateStatic;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementTabType;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@SuppressWarnings("MissingUnique")
@Mixin(AdvancementsScreen.class)
public abstract class AdvancementsScreenMixin {
	
	@Shadow @Final private Map<Advancement, AdvancementTab> tabs;
	@Shadow @Final public static int WINDOW_WIDTH;
	@CreateStatic
	private static int tabPage, maxPages;
	
	@Inject(method = "init", at = @At("TAIL"))
	protected void init(CallbackInfo ci) {
		if (this.tabs.size() > AdvancementTabType.MAX_TABS) {
			var thiz = (Screen) (Object) this;
			
			int guiLeft = (thiz.width - 252) / 2;
			int guiTop = (thiz.height - 140) / 2;
			
			thiz.addRenderableWidget(Button.builder(Component.literal("<"), b -> tabPage = Math.max(tabPage - 1, 0))
				.pos(guiLeft, guiTop - 50).size(20, 20).build());
			thiz.addRenderableWidget(Button.builder(Component.literal(">"), b -> tabPage = Math.min(tabPage + 1, maxPages))
				.pos(guiLeft + WINDOW_WIDTH - 20, guiTop - 50).size(20, 20).build());
			maxPages = tabs.size() / AdvancementTabType.MAX_TABS;
		}
	}
	
	@WrapOperation(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;isMouseOver(IIDD)Z"))
	public boolean mouseClicked$isMouseOver(AdvancementTab instance, int i, int j, double d, double e, Operation<Boolean> original) {
		return instance.getPage() == tabPage && original.call(instance, i, j, d, e);
	}
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementsScreen;renderBackground(Lnet/minecraft/client/gui/GuiGraphics;)V", shift = At.Shift.AFTER))
	public void render$addPageLabel(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
		if (maxPages != 0) {
			var thiz = (Screen) (Object) this;
			
			Component page = Component.literal(String.format("%d / %d", tabPage + 1, maxPages + 1));
			int width = thiz.font.width(page);
			guiGraphics.drawString(thiz.font, page.getVisualOrderText(), i + (252 / 2) - (width / 2), j - 44, -1);
		}
	}
	
	@ShadowSuper("render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V")
	public abstract void super$render(GuiGraphics guiGraphics, int i, int j, float f);
	
	@Inject(method = "render", at = @At("TAIL"))
	public void render$render(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
		super$render(guiGraphics, i, j, f);
	}
	
	@WrapWithCondition(method = "renderWindow", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;drawTab(Lnet/minecraft/client/gui/GuiGraphics;IIZ)V"))
	public boolean renderWinder$drawTab(AdvancementTab instance, GuiGraphics guiGraphics, int i, int j, boolean bl) {
		return instance.getPage() == tabPage;
	}
	
	@WrapWithCondition(method = "renderWindow", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;drawIcon(Lnet/minecraft/client/gui/GuiGraphics;II)V"))
	public boolean renderWinder$drawIcon(AdvancementTab instance, GuiGraphics guiGraphics, int i, int j) {
		return instance.getPage() == tabPage;
	}
	
	@WrapOperation(method = "renderTooltips", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;isMouseOver(IIDD)Z"))
	public boolean renderTooltips$isMouseOver(AdvancementTab instance, int i, int j, double d, double e, Operation<Boolean> original) {
		return instance.getPage() == tabPage && original.call(instance, i, j, d, e);
	}
	
}
