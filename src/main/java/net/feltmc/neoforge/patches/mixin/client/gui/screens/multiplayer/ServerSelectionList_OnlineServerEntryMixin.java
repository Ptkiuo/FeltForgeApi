package net.feltmc.neoforge.patches.mixin.client.gui.screens.multiplayer;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public abstract class ServerSelectionList_OnlineServerEntryMixin extends ServerSelectionList.Entry {
	@Shadow
	@Final
	private JoinMultiplayerScreen screen;
	
	@Shadow
	@Final
	private ServerData serverData;
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;touchscreen()Lnet/minecraft/client/OptionInstance;"))
	private void render$ping(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, int n, int o, boolean bl,
	                         float f, CallbackInfo ci, @Local(ordinal = 10) int i1, @Local(ordinal = 11) int j1) {
		net.minecraftforge.client.ForgeHooksClient.drawForgePingInfo(this.screen, serverData, guiGraphics, k,
			j, l, i1, j1); // TODO: Make sure the right args are being passed.
	}
}
