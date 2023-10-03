package net.feltmc.neoforge.patches.mixin.client;

import com.mojang.blaze3d.platform.InputConstants;
import fr.catcore.cursedmixinextensions.annotations.NewConstructor;
import fr.catcore.cursedmixinextensions.annotations.ReplaceConstructor;
import fr.catcore.cursedmixinextensions.annotations.ShadowConstructor;
import net.feltmc.feltasm.asm.CreateStatic;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.extensions.IForgeKeyMapping;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyMappingLookup;
import net.minecraftforge.client.settings.KeyModifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@SuppressWarnings({ "MissingUnique", "FieldMayBeFinal", "AddedMixinMembersNamePattern", "FieldCanBeLocal" })
@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin implements IForgeKeyMapping {
	
	@Shadow private InputConstants.Key key;
	@Shadow @Final private String category;
	@Shadow @Final private String name;
	@Shadow @Final private static Map<String, Integer> CATEGORY_SORT_ORDER;
	@ShadowConstructor public abstract void thiz(String string, InputConstants.Type type, int i, String string2);
	
	@CreateStatic
	private static KeyMappingLookup MAP = new KeyMappingLookup();
	
	private KeyModifier keyModifierDefault = KeyModifier.NONE;
	private KeyModifier keyModifier = KeyModifier.NONE;
	private IKeyConflictContext keyConflictContext = net.minecraftforge.client.settings.KeyConflictContext.UNIVERSAL;
	
	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void static$TAIL(CallbackInfo ci) {
		MAP = new KeyMappingLookup();
	}
	
	@SuppressWarnings("OverwriteAuthorRequired")
	@Overwrite
	public int compareTo(KeyMapping keyMapping) {
		if (this.category.equals(keyMapping.getCategory()))
			return I18n.get(this.name).compareTo(I18n.get(keyMapping.getName()));
		Integer tCat = CATEGORY_SORT_ORDER.get(this.category);
		Integer oCat = CATEGORY_SORT_ORDER.get(keyMapping.getCategory());
		if (tCat == null && oCat != null)
			return 1;
		if (tCat != null && oCat == null)
			return -1;
		if (tCat == null)
			return I18n.get(this.category).compareTo(I18n.get(keyMapping.getCategory()));
		return tCat.compareTo(oCat);
	}
	
	@Inject(method = "same", at = @At("HEAD"), cancellable = true)
	public void same$HEAD(KeyMapping keyMapping, CallbackInfoReturnable<Boolean> cir) {
		if (getKeyConflictContext().conflicts(keyMapping.getKeyConflictContext()) || keyMapping.getKeyConflictContext().conflicts(getKeyConflictContext())) {
			var keyModifier = getKeyModifier();
			var otherKeyModifier = keyMapping.getKeyModifier();
			if (keyModifier.matches(keyMapping.getKey()) || otherKeyModifier.matches(getKey())) {
				cir.setReturnValue(true);
			} else if (getKey().equals(keyMapping.getKey())) {
				// IN_GAME key contexts have a conflict when at least one modifier is NONE.
				// For example: If you hold shift to crouch, you can still press E to open your inventory. This means that a Shift+E hotkey is in conflict with E.
				// GUI and other key contexts do not have this limitation.
				cir.setReturnValue(keyModifier == otherKeyModifier ||
					(getKeyConflictContext().conflicts(KeyConflictContext.IN_GAME) &&
					(keyModifier == KeyModifier.NONE || otherKeyModifier == KeyModifier.NONE)));
			}
		}
	}
	
	@Inject(method = "getTranslatedKeyMessage", at = @At("TAIL"), cancellable = true)
	public void getTranslatedKeyMessage(CallbackInfoReturnable<Component> cir) {
		final Component originalReturnValue = cir.getReturnValue();
		cir.setReturnValue(getKeyModifier().getCombinedName(key, () -> originalReturnValue));
	}
	
	@Inject(method = "isDefault", at = @At("TAIL"), cancellable = true)
	public void isDefault$TAIL(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(cir.getReturnValue() && getKeyModifier() == getDefaultKeyModifier());
	}
	
	/**
	* Convenience constructor for creating KeyBindings with keyConflictContext set.
	*/
	@NewConstructor
	@ReplaceConstructor
	public void thiz(String description, IKeyConflictContext keyConflictContext, final InputConstants.Type inputType, final int keyCode, String category) {
		thiz(description, keyConflictContext, inputType.getOrCreate(keyCode), category);
	}
	
	/**
	* Convenience constructor for creating KeyBindings with keyConflictContext set.
	*/
	@NewConstructor
	@ReplaceConstructor
	public void thiz(String description, IKeyConflictContext keyConflictContext, InputConstants.Key keyCode, String category) {
		thiz(description, keyConflictContext, KeyModifier.NONE, keyCode, category);
	}
	
	/**
	* Convenience constructor for creating KeyBindings with keyConflictContext and keyModifier set.
	*/
	@NewConstructor
	@ReplaceConstructor
	public void thiz(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, final InputConstants.Type inputType, final int keyCode, String category) {
		thiz(description, keyConflictContext, keyModifier, inputType.getOrCreate(keyCode), category);
	}
	
	/**
	* Convenience constructor for creating KeyBindings with keyConflictContext and keyModifier set.
	*/
	@NewConstructor
	@ReplaceConstructor
	public void thiz(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, InputConstants.Key keyCode, String category) {
		thiz(description, keyCode.getType(), keyCode.getValue(), category);
		this.keyConflictContext = keyConflictContext;
		this.keyModifier = keyModifier;
		this.keyModifierDefault = keyModifier;
		if (this.keyModifier.matches(keyCode))
			this.keyModifier = KeyModifier.NONE;
		MAP.put(key, (KeyMapping) (Object) this);
	}
	
	@Override
	public InputConstants.@NotNull Key getKey() {
		return this.key;
	}
	
	@Override
	public void setKeyConflictContext(@NotNull IKeyConflictContext keyConflictContext) {
		this.keyConflictContext = keyConflictContext;
	}
	
	@Override
	public @NotNull IKeyConflictContext getKeyConflictContext() {
		return this.keyConflictContext;
	}
	
	@Override
	public @NotNull KeyModifier getDefaultKeyModifier() {
		return this.keyModifierDefault;
	}
	
	@Override
	public @NotNull KeyModifier getKeyModifier() {
		return this.keyModifier;
	}
	
	@Override
	public void setKeyModifierAndCode(@NotNull KeyModifier keyModifier, InputConstants.@NotNull Key keyCode) {
		KeyMapping.MAP.remove(this.key);
		this.key = keyCode;
		if (keyModifier.matches(keyCode))
			keyModifier = KeyModifier.NONE;
		MAP.remove((KeyMapping) (Object) this);
		this.keyModifier = keyModifier;
		MAP.put(keyCode, (KeyMapping) (Object) this);
		KeyMapping.MAP.put(keyCode, (KeyMapping) (Object) this);
	}
}
