package net.feltmc.neoforge.patches.mixin.client.gui.screens;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    protected ChatScreenMixin(Component component) {
        super(component);
    }

    @ModifyReturnValue(method = "handleChatInput", at = @At(value = "RETURN", ordinal = 1))
    private boolean handleChatInputNotClose(boolean original) {
        return original && this.minecraft.screen == this;
    }
}
