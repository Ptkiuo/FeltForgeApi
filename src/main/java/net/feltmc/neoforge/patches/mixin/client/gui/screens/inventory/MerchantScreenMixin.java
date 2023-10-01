package net.feltmc.neoforge.patches.mixin.client.gui.screens.inventory;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<net.minecraft.world.inventory.MerchantMenu> {
	public MerchantScreenMixin(MerchantMenu abstractContainerMenu, Inventory inventory, Component component) {
		super(abstractContainerMenu, inventory, component);
	}
	
	@Redirect(method = "renderAndDecorateCostA", at = @At(value = "INVOKE", 
		target = "Lnet/minecraft/client/gui/GuiGraphics;renderItemDecorations(Lnet/minecraft/client/gui/Font;" +
			"Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", ordinal = 1))
	private void renderAndDecorateCostA$forge(GuiGraphics guiGraphics, Font font, ItemStack itemStack, int i14, int j,
	                                          String string) {
		// Forge: fixes Forge-8806, code for count rendering taken from GuiGraphics#renderGuiItemDecorations
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
		String count = string != null ? string : String.valueOf(itemStack.getCount());
		font.drawInBatch(
			count,
			(float) i14 + 19 - 2 - font.width(count), 
			(float)j + 6 + 3, 
			0xFFFFFF, 
			true, 
			guiGraphics.pose().last().pose(), 
			guiGraphics.bufferSource(), 
			net.minecraft.client.gui.Font.DisplayMode.NORMAL, 
			0, 15728880, false
		);
		guiGraphics.pose().popPose();
	}
}
