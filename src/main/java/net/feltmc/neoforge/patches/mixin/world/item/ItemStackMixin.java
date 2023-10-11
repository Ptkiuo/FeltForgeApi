package net.feltmc.neoforge.patches.mixin.world.item;

import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fr.catcore.cursedmixinextensions.annotations.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.extensions.IForgeItemStack;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({ "MissingUnique", "FieldCanBeLocal", "AddedMixinMembersNamePattern" })
@ChangeSuperClass(CapabilityProvider.class)
@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IForgeItemStack {
	
	@SuppressWarnings("SameParameterValue")
	@ShadowSuperConstructor
	protected static void zuper(final Class<?> baseClass, final boolean isLazy) {}
	
	@ShadowSuper(value = "gatherCapabilities()V", remap = false)
	protected abstract void gatherCapabilities();
	
	@ShadowSuper(value = "gatherCapabilities(Ljava/util/function/Supplier;)V", remap = false)
	protected abstract void gatherCapabilities(@Nullable Supplier<ICapabilityProvider> parent);
	
	@ShadowSuper(value = "serializeCaps()Lnet/minecraft/nbt/CompoundTag;", remap = false)
	protected abstract @Nullable CompoundTag serializeCaps();
	
	@ShadowSuper(value = "deserializeCaps(Lnet/minecraft/nbt/CompoundTag;)V", remap = false)
	protected abstract void deserializeCaps(CompoundTag tag);
	
	@ShadowSuper(value = "areCapsCompatible(Lnet/minecraftforge/common/capabilities/CapabilityProvider;)Z", remap = false)
	public abstract boolean areCapsCompatible(CapabilityProvider<?> other);
	
	@SuppressWarnings("DeprecatedIsStillUsed")
	@Shadow @Final @Deprecated
	private @Nullable Item item;
	
	@Shadow
	public abstract void setTag(@Nullable CompoundTag compoundTag);
	
	@Shadow
	public abstract Item getItem();
	
	@Shadow
	public abstract InteractionResult useOn(UseOnContext useOnContext);
	
	@Shadow
	public abstract Rarity getRarity();
	
	@ShadowConstructor
	public abstract void thiz(ItemLike itemLike, int count);
	
	@Nullable
	private Holder.Reference<Item> delegate;
	private CompoundTag capNBT;
	
	@Redirect(
		method = {
			"<init>(Ljava/lang/Void;)V", 
			"<init>(Lnet/minecraft/world/level/ItemLike;I)V",
			"<init>(Lnet/minecraft/nbt/CompoundTag;)V"}, 
		at = @At(value = "INVOKE", target = "Ljava/lang/Object;<init>()V"))
	private static void this$super(Object instance) {
		zuper(ItemStack.class, true);
	}
	
	@Inject(
		method = {
			"<init>(Lnet/minecraft/world/level/ItemLike;I)V",
			"<init>(Lnet/minecraft/nbt/CompoundTag;)V"}, 
		at = @At(
			value = "FIELD", 
			target = "Lnet/minecraft/world/item/ItemStack;count:I", 
			shift = At.Shift.AFTER))
	public void this$count(CallbackInfo ci) {
		this.delegate = ForgeRegistries.ITEMS.getDelegateOrThrow(this.item);
		this.forgeInit();
	}
	
	@WrapOperation(
		method = {
			"<init>(Lnet/minecraft/world/level/ItemLike;I)V",
			"<init>(Lnet/minecraft/nbt/CompoundTag;)V"}, 
		at = @At(
			value = "INVOKE", 
			target = "Lnet/minecraft/world/item/Item;canBeDepleted()Z"))
	private boolean this$canBeDepleted(Item instance, Operation<Boolean> original) {
		return instance.isDamageable((ItemStack) (Object) this);
	}
	
	@NewConstructor
	@ReplaceConstructor
	public void thiz(ItemLike itemLike, int count, @Nullable CompoundTag capNBT) {
		this.capNBT = capNBT;
		thiz(itemLike, count);
	}
	
	@Inject(method = "<init>(Ljava/lang/Void;)V", at = @At("TAIL"))
	private void this$void$TAIL(Void void_, CallbackInfo ci) {
		this.delegate = null;
	}
	
	@Inject(
		method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", 
		at = @At(
			value = "INVOKE", 
			target = 
				"Lnet/minecraft/core/DefaultedRegistry;" +
				"get(Lnet/minecraft/resources/ResourceLocation;)Ljava/lang/Object;",
			shift = At.Shift.BEFORE))
	private void this$compoundTag(CompoundTag compoundTag, CallbackInfo ci) {
		this.capNBT = compoundTag.contains("ForgeCaps") ? compoundTag.getCompound("ForgeCaps") : null;
	}
	
	@WrapOperation(
		method = {"isEmpty", "getItem"}, 
		at = @At(
			value = "FIELD", 
			target = "Lnet/minecraft/world/item/ItemStack;item:Lnet/minecraft/world/item/Item;"))
	public Item delegateItem(ItemStack instance, Operation<Item> original) {
		assert this.delegate != null;
		return (Item) this.delegate.get();
	}
	
	@Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
	public void useOn$HEAD(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir,
	                       @Share("callback") LocalRef<Function<UseOnContext, InteractionResult>> callbackRef) {
		final Function<UseOnContext, InteractionResult> callback = callbackRef.get();
		if (callback == null) {
			if (!useOnContext.getLevel().isClientSide) {
				cir.setReturnValue(ForgeHooks.onPlaceItemIntoWorld(useOnContext));
				return;
			}
			callbackRef.set((c) -> getItem().useOn(useOnContext));
		}
	}
	
	// TODO felt: just going to hope @Share works here
	public InteractionResult onItemUseFirst(UseOnContext useOnContext, 
	                                        @Share("callback") LocalRef<Function<UseOnContext, InteractionResult>> callbackRef) {
		callbackRef.set((c) -> getItem().onItemUseFirst((ItemStack) (Object) this, c));
		return useOn(useOnContext);
	}
	
	private InteractionResult onItemUse(UseOnContext useOnContext, Function<UseOnContext, InteractionResult> callback,
	                                   @Share("callback") LocalRef<Function<UseOnContext, InteractionResult>> callbackRef) {
		callbackRef.set(callback);
		return useOn(useOnContext);
	}
	
	@WrapOperation(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;useOn" +
		"(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;"))
	public InteractionResult useOn$useOn(Item instance, UseOnContext useOnContext,
	                                     Operation<InteractionResult> original,
	                                     @Share("callback") LocalRef<Function<UseOnContext, InteractionResult>> callbackRef) {
		return callbackRef.get().apply(useOnContext);
	}
	
	@Inject(method = "save", at = @At("RETURN"))
	public void save$RETURN(CompoundTag compoundTag, CallbackInfoReturnable<CompoundTag> cir) {
		final CompoundTag cnbt = this.serializeCaps();
		if (cnbt != null && !cnbt.isEmpty()) {
			compoundTag.put("ForgeCaps", cnbt);
		}
	}
	
	@WrapOperation(method = "getMaxStackSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;" +
		"getMaxStackSize()I"))
	public int getMaxStackSize$getMaxStackSize(Item instance, Operation<Integer> original) {
		return instance.getMaxStackSize((ItemStack) (Object) this);
	}
	
	@Redirect(method = "isDamageableItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getMaxDamage()I"))
	public int isDamageableItem$getMaxDamage(Item instance) {
		return instance.isDamageable((ItemStack) (Object) this) ? 1 : 0;
	}
	
	@Redirect(method = "isDamaged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getDamageValue()I"))
	public int isDamaged$getDamageValue(ItemStack instance) {
		return instance.getItem().isDamaged((ItemStack) (Object) this) ? 1 : 0;
	}
	
	/**
	 * @author  Nolij
	 * @reason  There's no way to do this without breaking mixin compat anyway
	 */
	@Overwrite
	public int getDamageValue() {
		return this.getItem().getDamage((ItemStack) (Object) this);
	}
	
	@Inject(method = "setDamageValue", at = @At("TAIL"))
	public void setDamageValue$TAIL(int i, CallbackInfo ci) {
		this.getItem().setDamage((ItemStack) (Object) this, i);
	}
	
	@WrapOperation(
		method = "getMaxDamage", 
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getMaxDamage()I"))
	public int getMaxDamage$getMaxDamage(Item instance, Operation<Integer> original) {
		return instance.getMaxDamage((ItemStack) (Object) this);
	}
	
	@ModifyArg(
		method = "hurtAndBreak", 
		at = @At(
			value = "INVOKE", 
			target = "Lnet/minecraft/world/item/ItemStack;hurt(ILnet/minecraft/util/RandomSource;" +
				"Lnet/minecraft/server/level/ServerPlayer;)Z"), 
		index = 0)
	public <T extends LivingEntity> int hurtAndBreak$hurt(int i, @Local T livingEntity, @Local Consumer<T> consumer) {
		return this.getItem().damageItem((ItemStack) (Object) this, i, livingEntity, consumer);
	}
	
	@WrapOperation(
		method = "isCorrectToolForDrops",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/Item;isCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	public boolean isCorrectToolForDrops$isCorrectToolForDrops(Item instance, BlockState blockState,
	                                                           Operation<Boolean> original) {
		return instance.isCorrectToolForDrops((ItemStack) (Object) this, blockState);
	}
	
	@Redirect(method = "copy", at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/ItemLike;I)Lnet/minecraft/world/item/ItemStack;"))
	public ItemStack copy$NEW(ItemLike itemLike, int i) {
		return new ItemStack(itemLike, i, this.serializeCaps());
	}
	
	@ModifyExpressionValue(
		method = "isSameItemSameTags", 
		at = @At(value = "INVOKE", target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z"))
	private static boolean isSameItemSameTags$equals(Object o1, Object o2, boolean original) {
		//noinspection rawtypes,unchecked
		return original && ((CapabilityProvider) o1).areCapsCompatible((CapabilityProvider<?>) o2);
	}
	
	@WrapOperation(
		method = "setTag", 
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;canBeDepleted()Z"))
	public boolean setTag$canBeDepleted(Item instance, Operation<Boolean> original) {
		return instance.isDamageable((ItemStack) (Object) this);
	}
	
	@WrapOperation(
		method = {
			"getTooltipLines",
			"getDisplayName"},
		at = @At(
			value = "INVOKE", 
			target = "Lnet/minecraft/network/chat/MutableComponent;" +
				"withStyle(Lnet/minecraft/ChatFormatting;)Lnet/minecraft/network/chat/MutableComponent;"))
	public MutableComponent rarity$color(MutableComponent instance, ChatFormatting chatFormatting,
	                                     Operation<MutableComponent> original) {
		//noinspection unchecked
		return instance.withStyle(this.getRarity().getStyleModifier());
	}
	
	@Inject(method = "getTooltipLines", at = @At("TAIL"))
	public void getTooltipLines$TAIL(@Nullable Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir) {
		ForgeEventFactory.onItemTooltip((ItemStack) (Object) this, player, cir.getReturnValue(), tooltipFlag);
	}
	
	@ModifyConstant(method = "getHideFlags", constant = @Constant(intValue = 0))
	public int getHideFlags$0(int constant) {
		return this.getItem().getDefaultTooltipHideFlags((ItemStack) (Object) this);
	}
	
	@WrapOperation(
		method = "getAttributeModifiers", 
		at = @At(
			value = "INVOKE", 
			target = "Lnet/minecraft/world/item/Item;getDefaultAttributeModifiers(Lnet/minecraft/world/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"))
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers$getDefaultAttributeModifiers(Item instance,
	                                                                                                 EquipmentSlot equipmentSlot, 
	                                                                                                 Operation<Multimap<Attribute, AttributeModifier>> original) {
		return instance.getAttributeModifiers(equipmentSlot, (ItemStack) (Object) this);
	}
	
	@Inject(method = "getAttributeModifiers", at = @At("TAIL"), cancellable = true)
	public void getAttributeModifiers$TAIL(EquipmentSlot equipmentSlot, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> cir) {
		cir.setReturnValue(
			ForgeHooks.getAttributeModifiers((ItemStack) (Object) this, equipmentSlot, cir.getReturnValue()));
	}
	
	public void deserializeNBT(CompoundTag nbt) {
		final ItemStack itemStack = ItemStack.of(nbt);
		this.setTag(itemStack.getTag());
		if (((ItemStackMixin) (Object) itemStack).capNBT != null)
			deserializeCaps(((ItemStackMixin) (Object) itemStack).capNBT);
	}
	
	/**
	 * Set up forge's ItemStack additions.
	 */
	private void forgeInit() {
		if (this.delegate != null) {
			this.gatherCapabilities(() -> {
				assert item != null;
				return item.initCapabilities((ItemStack) (Object) this, this.capNBT);
			});
			if (this.capNBT != null) deserializeCaps(this.capNBT);
		}
	}
	
}
