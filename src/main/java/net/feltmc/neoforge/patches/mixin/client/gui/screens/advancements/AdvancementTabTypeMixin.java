package net.feltmc.neoforge.patches.mixin.client.gui.screens.advancements;

import fr.catcore.cursedmixinextensions.annotations.Public;
import net.feltmc.feltasm.asm.CreateStatic;
import net.minecraft.client.gui.screens.advancements.AdvancementTabType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(AdvancementTabType.class)
public abstract class AdvancementTabTypeMixin {
	
	@SuppressWarnings("MissingUnique")
	@CreateStatic
	@Public
	private static int MAX_TABS;
	
	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void ztatic(CallbackInfo ci) {
		MAX_TABS = Arrays.stream(AdvancementTabType.values()).mapToInt(e -> e.max).sum();
	}
	
}
