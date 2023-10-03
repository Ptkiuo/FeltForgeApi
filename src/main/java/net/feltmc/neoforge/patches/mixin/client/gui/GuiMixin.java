package net.feltmc.neoforge.patches.mixin.client.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.feltmc.neoforge.patches.interfaces.GuiInterface;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(Gui.class)
public abstract class GuiMixin implements GuiInterface {
    @Shadow @Final public Minecraft minecraft;

    @Shadow private int toolHighlightTimer;

    @Shadow private ItemStack lastToolHighlight;

    @Shadow public abstract Font getFont();

    @Shadow public int screenWidth;

    @Shadow public int screenHeight;

    @WrapOperation(method = "renderEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;showIcon()Z"))
    private boolean renderEffectsOnlyVisible(
            MobEffectInstance instance, Operation<Boolean> operation,
            @Share("renderer") LocalRef<IClientMobEffectExtensions> ref
            ) {
        var renderer = IClientMobEffectExtensions.of(instance);
        ref.set(renderer);

        return renderer.isVisibleInGui(instance) && operation.call(instance);
    }

    @Redirect(method = "renderEffects", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", remap = false))
    private <E> boolean renderEffectsOnlyWhenVisible(
            List<E> instance, E e,
            @Share("renderer") LocalRef<IClientMobEffectExtensions> ref,
            @Local(ordinal = 0) GuiGraphics guiGraphics,
            @Local(ordinal = 0) MobEffectInstance mobEffectInstance,
            @Local(ordinal = 0) int i,
            @Local(ordinal = 1) int j,
            @Local(ordinal = 0) float f

    ) {
        if (!ref.get().renderGuiIcon(mobEffectInstance, (Gui)(Object) this, guiGraphics, i, j, 0, f)) return instance.add(e);

        return false;
    }

    @Inject(method = "renderSelectedItemName", cancellable = true, at = @At("HEAD"))
    private void renderSelectedItemNameOverride(GuiGraphics guiGraphics, CallbackInfo ci) {
        this.renderSelectedItemName(guiGraphics, 0);
        ci.cancel();
    }

    @Override
    public void renderSelectedItemName(GuiGraphics guiGraphics, int yShift) {
        this.minecraft.getProfiler().push("selectedItemName");
        if (this.toolHighlightTimer > 0 && !this.lastToolHighlight.isEmpty()) {
            MutableComponent mutableComponent = Component.empty().append(this.lastToolHighlight.getHoverName()).withStyle(this.lastToolHighlight.getRarity().getStyleModifier());
            if (this.lastToolHighlight.hasCustomHoverName()) {
                mutableComponent.withStyle(ChatFormatting.ITALIC);
            }

            Component highlightTip = this.lastToolHighlight.getHighlightTip(mutableComponent);
            int i = this.getFont().width(highlightTip);
            int j = (this.screenWidth - i) / 2;
            int k = this.screenHeight - Math.max(yShift, 59);
            if (!this.minecraft.gameMode.canHurtPlayer()) {
                k += 14;
            }

            int l = (int)((float)this.toolHighlightTimer * 256.0F / 10.0F);
            if (l > 255) {
                l = 255;
            }

            if (l > 0) {
                int var10001 = j - 2;
                int var10002 = k - 2;
                int var10003 = j + i + 2;
                guiGraphics.fill(var10001, var10002, var10003, k + 9 + 2, this.minecraft.options.getBackgroundColor(0));
                Font font = net.minecraftforge.client.extensions.common.IClientItemExtensions.of(this.lastToolHighlight).getFont(this.lastToolHighlight, net.minecraftforge.client.extensions.common.IClientItemExtensions.FontContext.SELECTED_ITEM_NAME);
                if (font == null) {
                    guiGraphics.drawString(this.getFont(), highlightTip, j, k, 16777215 + (l << 24));
                } else {
                    j = (this.screenWidth - font.width(highlightTip)) / 2;
                    guiGraphics.drawString(font, highlightTip, j, k, 16777215 + (l << 24));
                }
            }
        }

        this.minecraft.getProfiler().pop();
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private boolean tick1(
            ItemStack instance,
            Item item,
            Operation<Boolean> operation
            ) {
        return instance.getItem() == item;
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Ljava/lang/Object;equals(Ljava/lang/Object;)Z", remap = false))
    private boolean tick2(
            Object instance, Object arg, Operation<Boolean> operation,
            @Local(ordinal = 0) ItemStack itemStack
    ) {
        return operation.call(instance, arg) && itemStack.getHighlightTip(itemStack.getHoverName())
                .equals(this.lastToolHighlight.getHighlightTip(this.lastToolHighlight.getHoverName()));
    }
}
