package net.feltmc.neoforge.patches.mixin.client.gui.screens;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Component component) {
        super(component);
    }

    private net.minecraftforge.client.gui.TitleScreenModUpdateIndicator modUpdateNotification;

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isDemo()Z"))
    private void initModButton(CallbackInfo ci, @Share("modButton") LocalRef<Button> modButton) {
        modButton.set(null);
    }

    @Inject(method = "init", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/screens/TitleScreen;createNormalMenuOptions(II)V"))
    private void createButton(CallbackInfo ci, @Share("modButton") LocalRef<Button> modButton, @Local(ordinal = 3) int l) {
        modButton.set(
                this.addRenderableWidget(Button.builder(
                        Component.translatable("fml.menu.mods"),
                        button -> this.minecraft.setScreen(
                                new net.minecraftforge.client.gui.ModListScreen(this)))
                        .pos(this.width / 2 - 100, l + 24 * 2)
                        .size(98, 20).build())
        );
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/TitleScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;", ordinal = 0))
    private void createModUpdateNotification(CallbackInfo ci, @Share("modButton") LocalRef<Button> modButton) {
        modUpdateNotification = net.minecraftforge.client.gui.TitleScreenModUpdateIndicator.init((TitleScreen)(Object) this, modButton.get());
    }

    @Inject(method = "render", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/gui/screens/TitleScreen;splash:Lnet/minecraft/client/gui/components/SplashRenderer;", ordinal = 0))
    private void renderForgeMainMenu(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci, @Local(ordinal = 2) int k) {
        net.minecraftforge.client.ForgeHooksClient.renderMainMenu((TitleScreen)(Object) this, guiGraphics, this.font, this.width, this.height, k);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)I"))
    private void wrapDrawString(GuiGraphics instance, Font font, @Nullable String string, int i_, int j_, int k_, Operation<Void> operation, @Local(ordinal = 2) int k) {
        net.minecraftforge.internal.BrandingControl.forEachLine(true, true, (brdline, brd) -> {
            operation.call(instance, this.font, brd, 2, this.height - ( 10 + brdline * (this.font.lineHeight + 1)), 16777215 | k);
        });

        net.minecraftforge.internal.BrandingControl.forEachAboveCopyrightLine((brdline, brd) -> {
            operation.call(instance, this.font, brd, this.width - font.width(brd), this.height - (10 + (brdline + 1) * ( this.font.lineHeight + 1)), 16777215 | k);
        });
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderEnd(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci, @Local(ordinal = 2) int k, @Local(ordinal = 1) float g) {
        if ((k & -67108864) != 0) {
            if (g >= 1.0f) modUpdateNotification.render(guiGraphics, i, j, f);
        }
    }
}
