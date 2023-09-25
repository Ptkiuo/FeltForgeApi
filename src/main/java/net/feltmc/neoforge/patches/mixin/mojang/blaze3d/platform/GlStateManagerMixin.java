package net.feltmc.neoforge.patches.mixin.mojang.blaze3d.platform;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.GlStateManager;
import net.feltmc.feltasm.asm.CreateStatic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("MissingUnique")
@Mixin(GlStateManager.class)
public class GlStateManagerMixin {
    @CreateStatic
    private static float lastBrightnessX = 0.0f;
    @CreateStatic
    private static float lastBrightnessY = 0.0f;


    @Inject(method = "_texParameter(IIF)V", at = @At("TAIL"), remap = false)
    private static void texParamMixin(CallbackInfo info, @Local(ordinal = 1) int x, @Local float y) {
        lastBrightnessX = x;
        lastBrightnessY = y;
    }
}
