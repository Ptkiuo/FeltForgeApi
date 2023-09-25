package net.feltmc.neoforge.patches.mixin.world.item;

import fr.catcore.cursedmixinextensions.annotations.NewConstructor;
import fr.catcore.cursedmixinextensions.annotations.ShadowConstructor;
import net.feltmc.feltasm.asm.CreateStatic;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.IExtensibleEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.UnaryOperator;

@SuppressWarnings("MissingUnique")
@Mixin(Rarity.class)
public abstract class RarityMixin implements IExtensibleEnum {
	
	@SuppressWarnings("FieldCanBeLocal")
	private UnaryOperator<Style> styleModifier;
	
	@ShadowConstructor
	public abstract void thiz(String key, int value, ChatFormatting chatFormatting);
	
	@Inject(method = "<init>(Ljava/lang/String;ILnet/minecraft/ChatFormatting;)V", at = @At("TAIL"))
	private void thiz(String key, int value, ChatFormatting chatFormatting, CallbackInfo ci) {
		styleModifier = style -> style.withColor(chatFormatting);
	}
	
	@NewConstructor
	private void thiz(String key, int value, UnaryOperator<Style> styleModifier) {
		thiz(key, value, ChatFormatting.BLACK);
		this.styleModifier = styleModifier;
	}
	
	public UnaryOperator<Style> getStyleModifier() {
		return styleModifier;
	}
	
	@CreateStatic
	public Rarity create(String name, ChatFormatting color) {
		throw new IllegalStateException("Enum not extended");
	}
	
	@CreateStatic
	public Rarity create(String name, UnaryOperator<Style> styleModifier) {
		throw new IllegalStateException("Enum not extended");
	}
	
}
