package net.feltmc.neoforge.patches.mixin.client.gui.screens.controls;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.feltmc.neoforge.patches.mixin.client.gui.screens.controls.KeyBindsListMixin.NAME_SPLIT_LENGTH;

@Mixin(KeyBindsList.KeyEntry.class)
public abstract class KeyBindsList$KeyEntryMixin extends KeyBindsList.Entry {
	@Shadow
	@Final
	private KeyMapping key;
	
	@Shadow
	@Final
	KeyBindsList field_2742;
	
	@Shadow
	@Final
	private Component name;
	
	@ModifyArg(method = "<init>", index = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/Button$Builder;bounds(IIII)Lnet/minecraft/client/gui/components/Button$Builder;", ordinal = 0))
    private int modifyBounds(int i) {
        return i + 20;
    }
	
	@Inject(method = "method_19870", at = @At("HEAD"))
	private void injectIntoSynthetic(KeyMapping keyMapping, Button button, CallbackInfo ci) {
		this.key.setToDefault();
	}
	
	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString" +
		"(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I"))
	private int trimStringsThatAreTooLong(GuiGraphics instance, Font font, Component component, int var10003, int mj,
	                                      int mk, boolean mbl, Operation<Integer> operation,
	                                      @Local(ordinal = 5) int n, @Local(ordinal = 6) int o) {
		// Neo: Trim strings that are too long, and show a tooltip if the mouse is over the trimmed string
		List<FormattedText> lines = font.getSplitter()
			.splitLines(component, NAME_SPLIT_LENGTH, Style.EMPTY);
		Component nameComponent = lines.size() > 1 ? Component.literal(lines.get(0).getString() + "...") : this.name;
		
		if(lines.size() > 1 && this.isMouseOver(n + 95, o) && n < var10003) {
			this.field_2742.keyBindsScreen.setTooltipForNextRenderPass(net.minecraft.locale.Language.getInstance().getVisualOrder(lines));
		}
		
		return operation.call(instance, font, nameComponent, var10003, mj,
			mk, mbl);
	}
	
	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components" +
		"/Button;setX(I)V", ordinal = 0))
	private void adaptX(Button instance, int arg, Operation<Void> operation) {
		operation.call(instance, arg + 20);
	}
	
	@WrapOperation(method = "refreshEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;same(Lnet/minecraft/client/KeyMapping;)Z"))
	private boolean conflictHandler(KeyMapping key, KeyMapping arg, Operation<Boolean> operation) {
		return operation.call(key, arg) || arg.hasKeyModifierConflict(key);
	}
	
	@WrapOperation(method = "refreshEntry", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens" +
		"/controls/KeyBindsList$KeyEntry;key:Lnet/minecraft/client/KeyMapping;", opcode = Opcodes.GETFIELD, 
		ordinal = 3))
	private KeyMapping conflictHandler(KeyBindsList.KeyEntry instance, Operation<KeyMapping> value,
	                                @Local(ordinal = 0) KeyMapping arg) {
		if (arg.hasKeyModifierConflict(value.call(instance))) {
			return null;
		}
		
		return value.call(instance);
	}
}
