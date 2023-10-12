package net.feltmc.neoforge.patches.mixin.client.multiplayer;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.authlib.minecraft.UserApiService;
import net.minecraft.client.multiplayer.AccountProfileKeyPairManager;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AccountProfileKeyPairManager.class)
public class AccountProfileKeyPairManagerMixin {
	@Shadow
	@Final
	private UserApiService userApiService;
	
	@WrapWithCondition(method = "method_44291", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error" +
		"(Ljava/lang/String;Ljava/lang/Throwable;)V", remap = false))
	private boolean onlyLogInProd(Logger instance, String text, Throwable throwable) {
		return net.minecraftforge.fml.loading.FMLLoader.isProduction() || this.userApiService != UserApiService.OFFLINE;
	}
}
