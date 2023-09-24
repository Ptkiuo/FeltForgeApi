package net.feltmc.neoforge.patches.mixin.client.gui.screens;

import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = {"net/minecraft/client/gui/screens/ConnectScreen$1"})
public abstract class ConnectScreen$1Mixin {
    @Shadow @Final
    ServerAddress val$hostAndPort;

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;execute(Ljava/lang/Runnable;)V", ordinal = 0))
    private void logConnection(CallbackInfo ci) {
        ConnectScreen.LOGGER.error("Couldn't connect to server: Unknown host \"{}\"", val$hostAndPort.getHost());
        net.minecraftforge.network.DualStackUtils.logInitialPreferences();
    }
}
