package net.feltmc.neoforge.patches.mixin.world.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.feltmc.neoforge.patches.interfaces.ItemInterface;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.GameData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

@SuppressWarnings({ "AddedMixinMembersNamePattern", "MissingUnique" })
@Mixin(Item.class)
public abstract class ItemMixin implements IForgeItem, ItemInterface {
	
	@Shadow @Final @Mutable public static Map<Block, Item> BY_BLOCK;
	
	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void static$TAIL(CallbackInfo ci) {
		BY_BLOCK = GameData.getBlockItemMap();
	}
	
	@Inject(method = "<init>", at = @At("TAIL"))
	public void this$TAIL(Item.Properties properties, CallbackInfo ci) {
		this.canRepair = properties.canRepair;
		initClient();
	}
	
	@WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;isEdible()Z"))
	public boolean use$isEdible(Item instance, Operation<Boolean> original,
	                            @Local Player player, @Local InteractionHand interactionHand,
	                            @Share("itemStack") LocalRef<ItemStack> itemStackRef) {
		var itemStack = player.getItemInHand(interactionHand);
		itemStackRef.set(itemStack);
		return itemStack.isEdible();
	}
	
	@WrapOperation(
		method = "use", 
		at = @At(
			value = "INVOKE", 
			target = "Lnet/minecraft/world/item/Item;getFoodProperties()Lnet/minecraft/world/food/FoodProperties;"))
	public FoodProperties use$getFoodProperties(Item instance, Operation<FoodProperties> original,
	                                            @Local Player player,
	                                            @Share("itemStack") LocalRef<ItemStack> itemStackRef) {
		return itemStackRef.get().getFoodProperties(player);
	}
	
	@Redirect(
		method = {
			"getBarWidth",
			"getBarColor"}, 
		at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/Item;maxDamage:I"))
	public int getBarWidth$maxDamage(Item instance, @Local ItemStack itemStack) {
		return instance.getMaxDamage(itemStack);
	}
	
	@WrapOperation(
		method = "getUseDuration", 
		at = @At(
			value = "INVOKE", 
			target = "Lnet/minecraft/world/item/Item;getFoodProperties()Lnet/minecraft/world/food/FoodProperties;"))
	public FoodProperties getUseDuration$getFoodProperties(Item instance, Operation<FoodProperties> original,
	                                                       @Local ItemStack itemStack) {
		return itemStack.getFoodProperties(null);
	}
	
	@WrapOperation(
		method = "isEnchantable", 
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getMaxStackSize()I"))
	public int isEnchantable$getMaxStackSize(Item instance, Operation<Integer> original, @Local ItemStack itemStack) {
		return instance.getMaxStackSize(itemStack);
	}
	
	@WrapOperation(
		method = "isEnchantable",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;canBeDepleted()Z"))
	public boolean isEnchantable$canBeDepleted(Item instance, Operation<Boolean> original, @Local ItemStack itemStack) {
		return instance.isDamageable(itemStack);
	}
	
	@ModifyConstant(
		method = "getPlayerPOVHitResult",
		constant = @Constant(doubleValue = 5D))
	private static double getPlayerPOVHitResult$5D(double constant, @Local Player player) {
		return player.getBlockReach();
	}
	
	protected boolean canRepair;
	
	@Override
	public boolean isRepairable(ItemStack stack) {
		return canRepair && isDamageable(stack);
	}
	
	@Inject(method = "useOnRelease", at = @At("TAIL"), cancellable = true)
	public void useOnRelease$TAIL(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(itemStack.getItem() == Items.CROSSBOW);
	}
	
	// FORGE START
	private Object renderProperties;
	
	/*
		DO NOT CALL, IT WILL DISAPPEAR IN THE FUTURE
		Call RenderProperties.get instead
	 */
	public Object getRenderPropertiesInternal() {
		return renderProperties;
	}
	
	private void initClient() {
		// Minecraft instance isn't available in datagen, so don't call initializeClient if in datagen
		if (FMLEnvironment.dist == Dist.CLIENT && !FMLLoader.getLaunchHandler().isData()) {
			initializeClient(properties -> {
				//noinspection EqualsBetweenInconvertibleTypes
				if (properties == this)
					throw new IllegalStateException("Don't extend IItemRenderProperties in your item, use an anonymous class instead.");
				this.renderProperties = properties;
			});
		}
	}
	
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
	}
	// END FORGE
	
	@SuppressWarnings("FieldCanBeLocal")
	@Mixin(Item.Properties.class)
	public static class PropertiesMixin {
		
		public boolean canRepair;
		
		@Inject(method = "<init>", at = @At("TAIL"))
		public void this$TAIL(CallbackInfo ci) {
			this.canRepair = true;
		}
		
		public Item.Properties setNoRepair() {
			this.canRepair = false;
			return (Item.Properties) (Object) this;
		}
		
	}
	
}
