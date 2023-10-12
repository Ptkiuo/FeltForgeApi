package net.feltmc.neoforge.patches.mixin.client.multiplayer;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import net.minecraftforge.network.ConnectionData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientHandshakePacketListenerImpl.class)
public abstract class ClientHandshakePacketListenerImplMixin implements ClientLoginPacketListener {
	@Shadow
	@Final
	private Connection connection;
	
	@Shadow
	@Final
	private @Nullable Screen parent;
	
	@Inject(method = "handleGameProfile", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;setListener(Lnet/minecraft/network/PacketListener;)V"))
	private void handleLogin(ClientboundGameProfilePacket clientboundGameProfilePacket, CallbackInfo ci) {
		net.minecraftforge.network.NetworkHooks.handleClientLoginSuccess(this.connection);
	}
	
	@ModifyArg(method = "onDisconnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;" +
		"setScreen(Lnet/minecraft/client/gui/screens/Screen;)V", ordinal = 1))
	private @Nullable Screen disconnectEvent(@Nullable Screen screen, @Local(ordinal = 0) Component component) {
		ConnectionData.ModMismatchData data = net.minecraftforge.network.NetworkHooks.getModMismatchData(connection);
		return data != null ?
		       new net.minecraftforge.client.gui.ModMismatchDisconnectedScreen(
				   this.parent, CommonComponents.CONNECT_FAILED,
			       component, data
		       ) : screen;
	}
	
	@Inject(method = "handleCustomQuery", cancellable = true, at = @At("HEAD"))
	private void cancelNegotiation(ClientboundCustomQueryPacket clientboundCustomQueryPacket, CallbackInfo ci) {
		if (net.minecraftforge.network.NetworkHooks.onCustomPayload(clientboundCustomQueryPacket, this.connection)) ci.cancel();
	}
}
