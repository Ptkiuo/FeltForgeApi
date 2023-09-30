package net.feltmc.neoforge.patches.mixin.client.gui.screens.controls;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBindsScreen.class)
public class KeyBindsScreenMixin extends OptionsSubScreen {
	@Shadow
	@Nullable
	public KeyMapping selectedKey;
	
	public KeyBindsScreenMixin(Screen screen, Options options, Component component) {
		super(screen, options, component);
	}
	
	@Redirect(method = "method_38532", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;setKey(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V"))
	private void resetKeyBind(KeyMapping instance, InputConstants.Key key) {
		instance.setToDefault();
	}
	
	@Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;setKey(Lnet/minecraft/client/KeyMapping;Lcom/mojang/blaze3d/platform/InputConstants$Key;)V"))
	private void setKeyModifierAndCodeUnknown(int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {
		this.selectedKey.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(),
			InputConstants.UNKNOWN);
	}
	
	@Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;setKey(Lnet/minecraft/client/KeyMapping;Lcom/mojang/blaze3d/platform/InputConstants$Key;)V"))
	private void setKeyModifierAndCodeKnown(int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {
		this.selectedKey.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(),
			InputConstants.getKey(i, j));
	}
	
	@WrapWithCondition(method = "keyPressed", at = @At(value = "FIELD", 
		target = "Lnet/minecraft/client/gui/screens/controls/KeyBindsScreen;" +
			"selectedKey:Lnet/minecraft/client/KeyMapping;", opcode = Opcodes.PUTFIELD))
	private boolean resetOnlyWhenNoModifier(KeyBindsScreen instance, KeyMapping newValue) {
		return !net.minecraftforge.client.settings.KeyModifier.isKeyCodeModifier(this.selectedKey.getKey());
	}
}
