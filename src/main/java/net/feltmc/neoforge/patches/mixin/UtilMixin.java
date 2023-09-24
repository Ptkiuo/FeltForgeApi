package net.feltmc.neoforge.patches.mixin;

import net.minecraft.Util;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Util.class)
public class UtilMixin {
	
	@Redirect(method = "doFetchChoiceType", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
	private static void doFetchChoiceType$error(Logger instance, String s, Object o) {
		instance.debug(s, o);
	}
	
}
