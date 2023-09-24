package net.feltmc.neoforge.patches.mixin.client.gui.screens;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.feltmc.neoforge.patches.interfaces.ScreenInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public abstract class ScreenMixin implements ScreenInterface {
    @Shadow @Final private List<GuiEventListener> children;

    @Shadow protected abstract void removeWidget(GuiEventListener guiEventListener);

    @Shadow @Nullable protected Minecraft minecraft;

    @Shadow @Final public List<Renderable> renderables;

    @Shadow @Final private List<NarratableEntry> narratables;

    @Redirect(method = "onClose", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"))
    private void closeTheForgeWay(Minecraft instance, Screen screen) {
        instance.popGuiLayer();
    }

    @Inject(method = "init(Lnet/minecraft/client/Minecraft;II)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/screens/Screen;init()V"))
    private void initPost(Minecraft minecraft, int i, int j, CallbackInfo ci) {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.Init.Post((Screen)(Object) this, this.children, this::addEventWidget, this::removeWidget));
    }

    @WrapWithCondition(method = "init(Lnet/minecraft/client/Minecraft;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;init()V"))
    private boolean initPre(Screen instance) {
        return !net.minecraftforge.common.MinecraftForge.EVENT_BUS
                .post(new net.minecraftforge.client.event.ScreenEvent.Init.Pre(
                        (Screen)(Object) this, this.children, this::addEventWidget, this::removeWidget));
    }

    @Inject(method = "rebuildWidgets", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/screens/Screen;init()V"))
    private void rebuildPost(CallbackInfo ci) {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.Init.Post((Screen)(Object) this, this.children, this::addEventWidget, this::removeWidget));
    }

    @WrapWithCondition(method = "rebuildWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;init()V"))
    private boolean rebuildPre(Screen instance) {
        return !net.minecraftforge.common.MinecraftForge.EVENT_BUS
                .post(new net.minecraftforge.client.event.ScreenEvent.Init.Pre(
                        (Screen)(Object) this, this.children, this::addEventWidget, this::removeWidget));
    }

    @Inject(method = "renderBackground", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/GuiGraphics;fillGradient(IIIIII)V"))
    private void backgroundRenderedEvent(GuiGraphics guiGraphics, CallbackInfo ci) {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.BackgroundRendered((Screen)(Object) this, guiGraphics));
    }

    @Inject(method = "renderDirtBackground", at = @At("RETURN"))
    private void dirtBackgroundRenderedEvent(GuiGraphics guiGraphics, CallbackInfo ci) {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.BackgroundRendered((Screen)(Object) this, guiGraphics));
    }

    @Override
    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    private void addEventWidget(GuiEventListener b) {
        if (b instanceof Renderable r) {
            this.renderables.add(r);
        }
        if (b instanceof NarratableEntry ne) {
            this.narratables.add(ne);
        }
        children.add(b);
    }
}
