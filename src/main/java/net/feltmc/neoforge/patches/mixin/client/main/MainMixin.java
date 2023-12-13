package net.feltmc.neoforge.patches.mixin.client.main;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.main.Main;
import net.minecraftforge.fml.loading.BackgroundWaiter;
import net.minecraftforge.fml.loading.FMLLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Main.class)
public class MainMixin {
	@WrapOperation(method = "main([Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/Bootstrap;" +
		"bootStrap()V"))
	private static void bootstrapInject(Operation<Void> original) {
		BackgroundWaiter.runAndTick(()-> original.call(), FMLLoader.progressWindowTick);
	}
}
