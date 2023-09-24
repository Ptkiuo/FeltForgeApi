package net.feltmc.neoforge.patches.mixin.client.gui.screens;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.Overlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public abstract class LoadingOverlayMixin extends Overlay {
    @Shadow private long fadeOutStart;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getMillis()J"))
    private long redirect() {
        return this.fadeOutStart;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/ReloadInstance;checkExceptions()V"))
    private void guard(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        this.fadeOutStart = Util.getMillis(); // Moved up to guard against inf loops caused by callback
    }
}
