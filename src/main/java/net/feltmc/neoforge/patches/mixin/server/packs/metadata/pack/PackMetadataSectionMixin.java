package net.feltmc.neoforge.patches.mixin.server.packs.metadata.pack;

import fr.catcore.cursedmixinextensions.annotations.NewConstructor;
import fr.catcore.cursedmixinextensions.annotations.ReplaceConstructor;
import fr.catcore.cursedmixinextensions.annotations.ShadowConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@SuppressWarnings("MissingUnique")
@Mixin(PackMetadataSection.class)
public abstract class PackMetadataSectionMixin {
	
	@Shadow @Final private Component description;
	@Shadow @Final private int packFormat;
	
	@ShadowConstructor
	public abstract void thiz(Component description, int packFormat);
	
	@SuppressWarnings("FieldCanBeLocal")
	private Map<PackType, Integer> packTypeVersions;
	
	@Inject(method = "<init>(Lnet/minecraft/network/chat/Component;I)V", at = @At("TAIL"))
	public void thiz(Component description, int packFormat, CallbackInfo ci) {
		this.packTypeVersions = Map.of();
	}
	
	@NewConstructor
	@ReplaceConstructor
	public void thiz(Component description, int packFormat, Map<PackType, Integer> packTypeVersions) {
		thiz(description, packFormat);
		this.packTypeVersions = packTypeVersions;
	}
	
	public int getPackFormat(PackType packType) {
		return packTypeVersions.getOrDefault(packType, this.packFormat);
	}
	
}
