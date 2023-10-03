package net.feltmc.neoforge.patches.mixin.client.gui.screens.inventory;

import com.google.common.collect.Ordering;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

@Mixin(EffectRenderingInventoryScreen.class)
public abstract class EffectRenderingInventoryScreenMixin<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
	public EffectRenderingInventoryScreenMixin(T abstractContainerMenu, Inventory inventory, Component component) {
		super(abstractContainerMenu, inventory, component);
	}
	
	@Inject(method = "renderEffects", cancellable = true, at = @At(value = "INVOKE", target = "Ljava/util/Collection;" +
		"size()I", ordinal = 0))
	private void renderEffects$onScreenPotionSize(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci,
	                                              @Local(ordinal = 2) LocalIntRef k,
	                                              @Local(ordinal = 3) LocalIntRef l,
	                                              @Local(ordinal = 0)LocalBooleanRef flag) {
		var event = net.minecraftforge.client.ForgeHooksClient.onScreenPotionSize(this, l.get(), !flag.get(), k.get());
		
		if (event.isCanceled()) ci.cancel();
		else {
			flag.set(!event.isCompact());
			k.set(event.getHorizontalOffset());
		}
	}
	
	@WrapOperation(method = "renderEffects", at = @At(
		value = "INVOKE", 
		target = "Lcom/google/common/collect/Ordering;sortedCopy(Ljava/lang/Iterable;)Ljava/util/List;", 
		remap = false))
	private Iterable<MobEffectInstance> renderEffects$sort(
		@SuppressWarnings("rawtypes") Ordering instance, Iterable<MobEffectInstance> collection) {
		return ((Collection<MobEffectInstance>) collection).stream()
			.filter(net.minecraftforge.client.ForgeHooksClient::shouldRenderEffect)
			.sorted()
			.collect(java.util.stream.Collectors.toList());
	}
	
	@WrapWithCondition(method = "renderIcons", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(IIIIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"))
	private boolean renderForgeIcons(GuiGraphics instance, int i_, int j_, int k_, int _l, int _m,
	                                 TextureAtlasSprite atlasSprite_,
	                                 @Local(ordinal = 0) MobEffectInstance mobeffectinstance,
	                                 @Local(ordinal = 2) int i) {
		var renderer = net.minecraftforge.client.extensions.common.IClientMobEffectExtensions.of(mobeffectinstance);

        return !renderer.renderInventoryIcon(
			mobeffectinstance, 
	        (EffectRenderingInventoryScreen<?>) (Object) this, 
	        instance, i_, i, 0);
    }
	
	@Inject(method = "renderLabels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/EffectRenderingInventoryScreen;getEffectName(Lnet/minecraft/world/effect/MobEffectInstance;)Lnet/minecraft/network/chat/Component;"))
	private void renderForgeLabelsInit(GuiGraphics guiGraphics, int i_, int j_, Iterable<MobEffectInstance> iterable,
	                                   CallbackInfo ci, @Local(ordinal = 0) MobEffectInstance mobeffectinstance,
	                                   @Local(ordinal = 2) int i, @Share("skip") LocalBooleanRef ref) {
		var renderer = net.minecraftforge.client.extensions.common.IClientMobEffectExtensions.of(mobeffectinstance);
		
		ref.set(renderer.renderInventoryText(
			mobeffectinstance, 
			(EffectRenderingInventoryScreen<?>)(Object) this, 
			guiGraphics, i_, i, 0));
	}
	
	@WrapWithCondition(method = "renderLabels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"))
	private boolean renderForgeLabels(GuiGraphics instance, Font font, Component component, int i, int j, int k,
	                                  @Share("skip") LocalBooleanRef ref) {
		return ref.get();
	}
}
