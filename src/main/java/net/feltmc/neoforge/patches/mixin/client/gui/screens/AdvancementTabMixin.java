package net.feltmc.neoforge.patches.mixin.client.gui.screens;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fr.catcore.cursedmixinextensions.annotations.NewConstructor;
import fr.catcore.cursedmixinextensions.annotations.ReplaceConstructor;
import fr.catcore.cursedmixinextensions.annotations.ShadowConstructor;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementTabType;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("MissingUnique")
@Mixin(AdvancementTab.class)
public abstract class AdvancementTabMixin {
	
	@SuppressWarnings("FieldCanBeLocal")
	private int page;
	
	@ShadowConstructor
	public abstract void thiz(Minecraft mc, AdvancementsScreen screen, AdvancementTabType type, int index, Advancement adv, DisplayInfo info);
	
	@NewConstructor
	@ReplaceConstructor
	public void thiz(Minecraft mc, AdvancementsScreen screen, AdvancementTabType type, int index, int page, Advancement adv, DisplayInfo info) {
		thiz(mc, screen, type, index, adv, info);
		this.page = page;
	}
	
	public int getPage() {
		return page;
	}
	
	// TODO [FELT]: verify this doesnt cause explosions 
	@ModifyVariable(method = "create", at = @At(value = "LOAD", ordinal = 0), name = "i", index = 2, argsOnly = true)
	private static int create$i(int value) {
		return value % AdvancementTabType.MAX_TABS;
	}
	
	@Redirect(method = "create", at = @At(value = "NEW", target = "(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/screens/advancements/AdvancementsScreen;Lnet/minecraft/client/gui/screens/advancements/AdvancementTabType;ILnet/minecraft/advancements/Advancement;Lnet/minecraft/advancements/DisplayInfo;)Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;"))
	private static AdvancementTab create$newAdvancementTab(Minecraft minecraft, AdvancementsScreen advancementsScreen, AdvancementTabType advancementTabType, int i, Advancement advancement, DisplayInfo displayInfo) {
		return new AdvancementTab(minecraft, advancementsScreen, advancementTabType, i % AdvancementTabType.MAX_TABS, i / AdvancementTabType.MAX_TABS, advancement, displayInfo);
	}
	
}
