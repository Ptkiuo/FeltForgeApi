package net.feltmc.neoforge.patches.mixin.client.gui.screens.inventory;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.platform.InputConstants;
import net.feltmc.feltasm.asm.CreateStatic;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("MissingUnique")
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen implements net.feltmc.neoforge.patches.interfaces.AbstractContainerScreenInterface {
	@Shadow
	@Nullable
	protected Slot hoveredSlot;
	
	@Shadow
	protected int leftPos;
	
	@Shadow
	protected int topPos;
	
	@Shadow
	protected int imageWidth;
	
	@Shadow
	protected int imageHeight;
	
	@Shadow
	protected abstract List<Component> getTooltipFromContainerItem(ItemStack itemStack);
	
	protected AbstractContainerScreenMixin(Component component) {
		super(component);
	}
	
	@Inject(method = "render", at = @At(
		value = "INVOKE", 
		target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableDepthTest()V", 
		remap = false))
	private void renderBackground(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ContainerScreenEvent.Render.Background(
			(AbstractContainerScreen)(Object) this, guiGraphics, i, j));
	}
	
	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;get(I)Ljava/lang/Object;"))
	private void render$pickUpSlotIndex(NonNullList instance, int i, Operation<Object> operation,
	                                    @Share("slotIndex")LocalRef<Integer> ref) {
		ref.set(i);
		operation.call(instance, i);
	}
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;III)V"))
	private void render$renderSlotHighlight(GuiGraphics guiGraphics, int i, int j, int k, @Share("slotIndex")LocalRef<Integer> ref) {
		renderSlotHighlight(guiGraphics, i, j, k, getSlotColor(ref.get()));
	}
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z",
		ordinal = 0))
	private void renderForeground(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ContainerScreenEvent.Render.Foreground(
			(AbstractContainerScreen)(Object) this, guiGraphics, i, j));
	}
	
	@Inject(method = "renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;III)V", cancellable = true, at = @At("HEAD"))
	private static void renderSlotHighlightOverride(GuiGraphics guiGraphics, int i, int j, int k, CallbackInfo ci) {
		AbstractContainerScreen.renderSlotHighlight(guiGraphics, i, j, k, -2130706433);
		ci.cancel();
	}
	
	@CreateStatic
	public void renderSlotHighlight(GuiGraphics p_283692_, int p_281453_, int p_281915_, int p_283504_,
	                                     int color) {
		p_283692_.fillGradient(RenderType.guiOverlay(), p_281453_, p_281915_, p_281453_ + 16, p_281915_ + 16, color, color,
			p_283504_);
	}
	
	@Redirect(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V"))
	private void renderTooltipExtra(GuiGraphics instance, Font font, List<Component> list,
	                                Optional<TooltipComponent> optional, int i, int j, @Local(ordinal = 0)ItemStack itemStack) {
		instance.renderTooltip(this.font, this.getTooltipFromContainerItem(itemStack), itemStack.getTooltipImage(),
			itemStack, i, j);
	}
	
	@ModifyArg(method = "renderFloatingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"))
	private Font renderFloatingItem$changeFont(Font original, @Local(ordinal = 0) ItemStack itemStack) {
		var font = net.minecraftforge.client.extensions.common.IClientItemExtensions.of(itemStack).getFont(itemStack,
			net.minecraftforge.client.extensions.common.IClientItemExtensions.FontContext.ITEM_COUNT);
		return font == null ? original : font;
	}
	
	@Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasInfiniteItems()Z"))
	private boolean mouseClicked$trick(MultiPlayerGameMode instance) {
		return true;
	}
	
	@Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;matchesMouse(I)Z"))
	private boolean mouseClicked$isActiveAndMatch(KeyMapping instance, int i) {
		InputConstants.Key mouseKey = InputConstants.Type.MOUSE.getOrCreate(i);
		return this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey);
	}
	
	@WrapOperation(method = "mouseClicked", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft" +
		"/client/gui/screens/inventory/AbstractContainerScreen;hasClickedOutside(DDIII)Z"))
	private boolean mouseClicked$hasClickedOutside(AbstractContainerScreen instance, double i, double j, 
	                                               int k, int l, int m, Operation<Boolean> operation, 
	                                               @Local(ordinal = 0) Slot slot) {
		boolean original = operation.call(instance, i, j, k, l, m);
		
		// Forge, prevent dropping of items through slots outside of GUI boundaries
		if (slot != null) {
			original = false;
		}
		
		return original;
	}
	
	@Inject(method = "mouseReleased", at = @At("HEAD"))
	private void mouseReleased$super(double d, double e, int i, CallbackInfoReturnable<Boolean> cir) {
		super.mouseReleased(d, e, i);
	}
	
	@WrapOperation(method = "mouseReleased", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;hasClickedOutside(DDIII)Z"))
	private boolean mouseReleased$changeFlag(AbstractContainerScreen instance, double i, double j, int k, int l,
	                                         int m, Operation<Boolean> operation, @Local(ordinal = 0) Slot slot,
	                                         @Share("mouseKey") LocalRef<InputConstants.Key> ref) {
		boolean original = operation.call(instance, i, j, k, l, m);
		
		if (slot != null) {
			original = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
		}
		
		ref.set(InputConstants.Type.MOUSE.getOrCreate(m));
		
		return original;
	}
	
	@WrapOperation(method = "mouseReleased", at = @At(value = "FIELD", target = "Lnet/minecraft/world/inventory/Slot;" +
		"container:Lnet/minecraft/world/Container;", ordinal = 0, opcode = Opcodes.GETFIELD))
	private Container equalityPart1(Slot slot2, Operation<Container> operation, @Local(ordinal = 0) Slot slot) {
		if (slot2.isSameInventory(slot)) {
			return null;
		}
		
		Container container = operation.call(slot2);
		return container == null ? operation.call(slot) : container;
	}
	
	@WrapOperation(method = "mouseReleased", at = @At(value = "FIELD", target = "Lnet/minecraft/world/inventory/Slot;" +
		"container:Lnet/minecraft/world/Container;", ordinal = 1, opcode = Opcodes.GETFIELD))
	private Container equalityPart2(Slot slot, Operation<Container> operation) {
        return null;
	}
	
	@Redirect(method = "mouseReleased", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;matchesMouse(I)Z"))
	private boolean mouseReleased$matchesMouse(KeyMapping instance, int i, @Share("mouseKey") LocalRef<InputConstants.Key> ref) {
		return instance.isActiveAndMatches(ref.get());
	}
	
	@Inject(method = "keyPressed", at = @At("HEAD"))
	private void keyPressed$mouseKey(int i, int j, int k, CallbackInfoReturnable<Boolean> cir,
	                                 @Share("mouseKey") LocalRef<InputConstants.Key> ref) {
		ref.set(InputConstants.getKey(i, j));
	}
	
	@Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;matches(II)Z"))
	private boolean keyPressed$matches(KeyMapping instance, int i, int j,
	                                   @Share("mouseKey") LocalRef<InputConstants.Key> ref) {
		return instance.isActiveAndMatches(ref.get());
	}
	
	@WrapOperation(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;checkHotbarKeyPressed(II)Z"))
	private boolean keyPressed$handled(AbstractContainerScreen instance, int i, int j, 
	                                   Operation<Boolean> operation,
	                                   @Share("mouseKey") LocalRef<InputConstants.Key> ref,
	                                   @Share("handled") LocalRef<Boolean> ref2) {
		boolean result = operation.call(instance, i, j);
		
		if (!(this.hoveredSlot != null && this.hoveredSlot.hasItem()) && this.minecraft.options.keyDrop.isActiveAndMatches(ref.get())) {
			result = true;
		}
		
		ref2.set(result);
		
		return result;
	}
	
	@Inject(method = "keyPressed", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ClickType;)V"))
	private void keyPressed$slotClicked(int i, int j, int k, CallbackInfoReturnable<Boolean> cir, 
	                                    @Share("handled") LocalRef<Boolean> ref) {
		ref.set(true);
	}
	
	@Inject(method = "keyPressed", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
	private void keyPressed$return(int i, int j, int k, CallbackInfoReturnable<Boolean> cir,
	                               @Share("handled") LocalRef<Boolean> ref) {
		cir.setReturnValue(ref.get());
	}
	
	@Redirect(method = "checkHotbarKeyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;matches(II)Z"))
	private boolean checkHotbarKeyPressed$matches(KeyMapping instance, int i, int j) {
		return instance.isActiveAndMatches(InputConstants.getKey(i, j));
	}
	
	@org.jetbrains.annotations.Nullable
	@Override
	public Slot getSlotUnderMouse() { return this.hoveredSlot; }
	@Override
	public int getGuiLeft() { return leftPos; }
	@Override
	public int getGuiTop() { return topPos; }
	@Override
	public int getXSize() { return imageWidth; }
	@Override
	public int getYSize() { return imageHeight; }
	
	protected int slotColor = -2130706433;
	@Override
	public int getSlotColor(int index) {
		return slotColor;
	}
}
