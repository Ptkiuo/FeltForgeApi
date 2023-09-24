package net.feltmc.neoforge.patches.mixin.client.gui.components.toasts;

import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToastComponent.class)
public class ToastComponentMixin {
    @Inject(method = "addToast", cancellable = true, at = @At("HEAD"))
    private void addToastEvent(Toast toast, CallbackInfo ci) {
        if (net.minecraftforge.client.ForgeHooksClient.onToastAdd(toast)) ci.cancel();
    }
}
