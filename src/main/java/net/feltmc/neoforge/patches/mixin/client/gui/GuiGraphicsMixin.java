package net.feltmc.neoforge.patches.mixin.client.gui;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.feltmc.neoforge.patches.interfaces.GuiGraphicsInterface;
import net.minecraft.CrashReportCategory;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RenderTooltipEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin implements net.minecraftforge.client.extensions.IForgeGuiGraphics, GuiGraphicsInterface {
    @Shadow @Final private PoseStack pose;

    @Shadow @Final private MultiBufferSource.BufferSource bufferSource;

    @Shadow @Deprecated protected abstract void flushIfUnmanaged();

    @Shadow
    private static IntIterator slices(int i, int j) {
        return null;
    }

    @Shadow
    public abstract void blit(ResourceLocation location, int i, int j, float k, float l, int m, int n, int o, int p);

    @Shadow public abstract void renderTooltip(Font font, List<Component> list, Optional<TooltipComponent> optional, int i, int j);

    @Shadow public abstract int guiWidth();

    @Shadow public abstract int guiHeight();

    @Shadow protected abstract void renderTooltipInternal(Font font, List<ClientTooltipComponent> list, int i, int j, ClientTooltipPositioner clientTooltipPositioner);

    @Inject(method = "drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I", at = @At("HEAD"), cancellable = true)
    private void overrideDrawString(Font font, String string, int i, int j, int k, boolean bl, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(this.drawString(font, string, (float) i, (float) j, k, bl));
    }

    @Override
    public int drawString(Font font, @Nullable String string, float i, float j, int k, boolean bl) {
        if (string == null) {
            return 0;
        } else {
            int l = font.drawInBatch(string, (float)i, (float)j, k, bl, this.pose.last().pose(), this.bufferSource, Font.DisplayMode.NORMAL, 0, 15728880, font.isBidirectional());
            this.flushIfUnmanaged();
            return l;
        }
    }

    @Inject(method = "drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;IIIZ)I", at = @At("HEAD"), cancellable = true)
    private void overrideDrawString(Font font, FormattedCharSequence formattedCharSequence, int i, int j, int k, boolean bl, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(this.drawString(font, formattedCharSequence, (float) i, (float) j, k, bl));
    }

    @Override
    public int drawString(Font font, FormattedCharSequence formattedCharSequence, float i, float j, int k, boolean bl) {
        int l = font.drawInBatch(formattedCharSequence, (float)i, (float)j, k, bl, this.pose.last().pose(), this.bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
        this.flushIfUnmanaged();
        return l;
    }

    @Inject(method = "blitRepeating(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V", at = @At("HEAD"), cancellable = true)
    private void overrideBlitRepeating(ResourceLocation resourceLocation, int i, int j, int k, int l, int m, int n, int o, int p, CallbackInfo ci) {
        this.blitRepeating(resourceLocation, i, j, k, l, m, n, o, p, 256, 256);
        ci.cancel();
    }

    @Override
    public void blitRepeating(ResourceLocation resourceLocation, int i, int j, int k, int l, int m, int n, int o, int p, int textureWidth, int textureHeight) {
        int q = i;

        int r;
        for(IntIterator intIterator = slices(k, o); intIterator.hasNext(); q += r) {
            r = intIterator.nextInt();
            int s = (o - r) / 2;
            int t = j;

            int u;
            for(IntIterator intIterator2 = slices(l, p); intIterator2.hasNext(); t += u) {
                u = intIterator2.nextInt();
                int v = (p - u) / 2;
                this.blit(resourceLocation, q, t, m + s, n + v, r, u, textureWidth, textureHeight);
            }
        }
    }

    @Inject(
            method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/CrashReportCategory;setDetail(Ljava/lang/String;Lnet/minecraft/CrashReportDetail;)Lnet/minecraft/CrashReportCategory;", ordinal = 1)
    )
    private void renderItemCrashInfo(
            LivingEntity livingEntity, Level level, ItemStack itemStack, int i, int j, int k, int l, CallbackInfo ci,
            @Local(ordinal = 0) CrashReportCategory crashReportCategory
    ) {
        crashReportCategory.setDetail("Registry Name", () -> String.valueOf(net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(itemStack.getItem())));
    }

    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
    at = @At(value = "INVOKE_ASSIGN", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"))
    private void renderItemDecorationsForge(Font font, ItemStack itemStack, int i, int j, String string, CallbackInfo ci) {
        net.minecraftforge.client.ItemDecoratorHandler.of(itemStack)
                .render((GuiGraphics)(Object) this, font, itemStack, i, j);
    }

    private ItemStack tooltipStack = ItemStack.EMPTY;

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"))
    private void renderTooltipSet(Font font, ItemStack itemStack, int i, int j, CallbackInfo ci) {
        this.tooltipStack = itemStack;
    }

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("RETURN"))
    private void renderTooltipUnset(Font font, ItemStack itemStack, int i, int j, CallbackInfo ci) {
        this.tooltipStack = ItemStack.EMPTY;
    }

    @Override
    public void renderTooltip(Font font, List<Component> textComponents, Optional<TooltipComponent> tooltipComponent, ItemStack stack, int mouseX, int mouseY) {
        this.tooltipStack = stack;
        this.renderTooltip(font, textComponents, tooltipComponent, mouseX, mouseY);
        this.tooltipStack = ItemStack.EMPTY;
    }

    @ModifyVariable(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V",
            ordinal = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"))
    private List<ClientTooltipComponent> getTooltipListForge(List<ClientTooltipComponent> list,
                                                             @Local(ordinal = 0) Font p_283128_, @Local(ordinal = 0) List<Component> p_282716_, @Local(ordinal = 0) Optional<TooltipComponent> p_281682_,
                                                             @Local(ordinal = 0) int p_283678_, @Local(ordinal = 1) int p_281696_
                                                             ) {
        return net.minecraftforge.client.ForgeHooksClient.gatherTooltipComponents(this.tooltipStack, p_282716_, p_281682_, p_283678_, guiWidth(), guiHeight(), p_283128_);
    }

    @Inject(method = "renderComponentTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;II)V",
    at = @At("HEAD"), cancellable = true)
    private void overrideRenderComponentTooltip(Font p_282739_, List<Component> p_281832_, int p_282191_, int p_282446_, CallbackInfo ci) {
        List<ClientTooltipComponent> components = net.minecraftforge.client.ForgeHooksClient.gatherTooltipComponents(this.tooltipStack, p_281832_, p_282191_, guiWidth(), guiHeight(), p_282739_);
        this.renderTooltipInternal(p_282739_, components, p_282191_, p_282446_, DefaultTooltipPositioner.INSTANCE);
        ci.cancel();
    }

    @Override
    public void renderComponentTooltip(Font font, List<? extends net.minecraft.network.chat.FormattedText> tooltips, int mouseX, int mouseY, ItemStack stack) {
        this.tooltipStack = stack;
        List<ClientTooltipComponent> components = net.minecraftforge.client.ForgeHooksClient.gatherTooltipComponents(stack, tooltips, mouseX, guiWidth(), guiHeight(), font);
        this.renderTooltipInternal(font, components, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE);
        this.tooltipStack = ItemStack.EMPTY;
    }

    @Inject(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 0, remap = false), cancellable = true)
    private void renderTooltipInternalPreEvent(
            Font p_282675_, List<ClientTooltipComponent> p_282615_, int p_283230_, int p_283417_, ClientTooltipPositioner p_282442_,
            CallbackInfo ci, @Share("preEvent") LocalRef<RenderTooltipEvent.Pre> ref) {
        net.minecraftforge.client.event.RenderTooltipEvent.Pre preEvent = net.minecraftforge.client.ForgeHooksClient.onRenderTooltipPre(this.tooltipStack, (GuiGraphics)(Object) this, p_283230_, p_283417_, guiWidth(), guiHeight(), p_282615_, p_282675_, p_282442_);

        if (preEvent.isCanceled()) ci.cancel();

        ref.set(preEvent);
    }

    @ModifyArg(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;getWidth(Lnet/minecraft/client/gui/Font;)I"))
    private Font renderTooltipInternalFont1(Font font, @Share("preEvent") LocalRef<RenderTooltipEvent.Pre> ref) {
        return ref.get().getFont();
    }

    @ModifyArg(method = "renderTooltipInternal", index = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;positionTooltip(IIIIII)Lorg/joml/Vector2ic;"))
    private int renderTooltipInternalX(int i, @Share("preEvent") LocalRef<RenderTooltipEvent.Pre> ref) {
        return ref.get().getX();
    }

    @ModifyArg(method = "renderTooltipInternal", index = 3, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;positionTooltip(IIIIII)Lorg/joml/Vector2ic;"))
    private int renderTooltipInternalY(int i, @Share("preEvent") LocalRef<RenderTooltipEvent.Pre> ref) {
        return ref.get().getY();
    }

    @ModifyArg(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawManaged(Ljava/lang/Runnable;)V"))
    private Runnable renderTooltipInternalReplaceManaged(Runnable runnable,
                                                         @Local(ordinal = 2) int i2, @Local(ordinal = 3) int j2, @Local(ordinal = 0) Vector2ic vector2ic, @Local(ordinal = 0) List<ClientTooltipComponent> p_282615_,
                                                         @Share("preEvent") LocalRef<RenderTooltipEvent.Pre> ref) {
        int l = vector2ic.x();
        int i1 = vector2ic.y();
        return () -> {
            net.minecraftforge.client.event.RenderTooltipEvent.Color colorEvent = net.minecraftforge.client.ForgeHooksClient.onRenderTooltipColor(this.tooltipStack, (GuiGraphics)(Object) this, l, i1, ref.get().getFont(), p_282615_);
            TooltipRenderUtil.renderTooltipBackground((GuiGraphics)(Object) this, l, i1, i2, j2, 400, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd(), colorEvent.getBorderStart(), colorEvent.getBorderEnd());
        };
    }

    @ModifyArg(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;renderText(Lnet/minecraft/client/gui/Font;IILorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V"))
    private Font renderTooltipInternalFont2(Font font, @Share("preEvent") LocalRef<RenderTooltipEvent.Pre> ref) {
        return ref.get().getFont();
    }

    @ModifyArg(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;renderImage(Lnet/minecraft/client/gui/Font;IILnet/minecraft/client/gui/GuiGraphics;)V"))
    private Font renderTooltipInternalFont3(Font font, @Share("preEvent") LocalRef<RenderTooltipEvent.Pre> ref) {
        return ref.get().getFont();
    }
}
