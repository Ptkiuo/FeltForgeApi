package net.feltmc.neoforge.patches.mixin.client.gui.screens.recipebook;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {
	@Shadow
	protected RecipeBookMenu<?> menu;
	
	@Redirect(method = "initVisuals", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/RecipeBookCategories;getCategories(Lnet/minecraft/world/inventory/RecipeBookType;)Ljava/util/List;"))
	private List<RecipeBookCategories> initVisuals$categories(RecipeBookType recipeBookType) {
		return this.menu.getRecipeBookCategories();
	}
	
	@Redirect(method = "renderGhostRecipeTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderComponentTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;II)V"))
	private void renderGhostRecipeTooltip$renderComponentTooltip(GuiGraphics instance, Font font,
	                                                             List<Component> list, int i, int j, 
	                                                             @Local(ordinal = 0) ItemStack itemStack) {
		instance.renderComponentTooltip(font, list, i, j, itemStack);
	}
}
