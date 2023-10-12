package net.feltmc.neoforge.patches.mixin.client.gui.screens.worldselection;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WorldSelectionList.WorldListEntry.class)
public abstract class WorldSelectionList_WorldListEntryMixin extends WorldSelectionList.Entry {
	@Shadow
	@Final
	private Minecraft minecraft;
	
	@Shadow
	@Final
	private LevelSummary summary;
	
	@Shadow
	@Final
	WorldSelectionList field_19135;
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;touchscreen()Lnet/minecraft/client/OptionInstance;"))
	private void render$warning(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f, CallbackInfo ci) {
		renderExperimentalWarning(guiGraphics, n, o, j, k);
	}
	
	@WrapWithCondition(method = "method_20170", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;" +
		"setScreen(Lnet/minecraft/client/gui/screens/Screen;)V", ordinal = 1))
	private boolean method_20170$conditional(Minecraft instance, Screen screen) {
		return this.minecraft.screen instanceof ProgressScreen;
	}
	
	// FORGE: Patch in experimental warning icon for worlds in the world selection screen
	private void renderExperimentalWarning(GuiGraphics guiGraphics, int mouseX, int mouseY, int top, int left) {
		if (this.summary.isLifecycleExperimental()) {
			int leftStart = left + this.field_19135.getRowWidth();
			guiGraphics.blit(WorldSelectionList.FORGE_EXPERIMENTAL_WARNING_ICON, leftStart - 36, top, 
				0.0F, 0.0F, 32, 32, 32, 32);
			if (this.field_19135.getEntryAtPosition(mouseX, mouseY) == this && mouseX > leftStart - 36 && mouseX < leftStart) {
				var font = Minecraft.getInstance().font;
				List<FormattedCharSequence> tooltip =
					font.split(Component.translatable("forge.experimentalsettings.tooltip"), 200);
				guiGraphics.renderTooltip(font, tooltip, mouseX, mouseY);
			}
		}
	}
}
