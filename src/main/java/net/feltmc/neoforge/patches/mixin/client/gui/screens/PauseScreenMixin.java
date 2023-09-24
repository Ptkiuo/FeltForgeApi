package net.feltmc.neoforge.patches.mixin.client.gui.screens;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {
    @Shadow @Final private static int BUTTON_WIDTH_FULL;

    protected PauseScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "createPauseMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isLocalServer()Z"))
    private void addModsButton(CallbackInfo ci, @Local(ordinal = 0) GridLayout.RowHelper rowHelper) {
        rowHelper.addChild(Button.builder(Component.translatable("fml.menu.mods"),
                button -> this.minecraft.setScreen(new net.minecraftforge.client.gui.ModListScreen(this)))
                .width(BUTTON_WIDTH_FULL).build(), 2);
    }
}
