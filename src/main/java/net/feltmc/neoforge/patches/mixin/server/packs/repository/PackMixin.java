package net.feltmc.neoforge.patches.mixin.server.packs.repository;

import com.llamalad7.mixinextras.sugar.Local;
import fr.catcore.cursedmixinextensions.annotations.NewConstructor;
import fr.catcore.cursedmixinextensions.annotations.ReplaceConstructor;
import fr.catcore.cursedmixinextensions.annotations.ShadowConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("MissingUnique")
@Mixin(Pack.class)
public class PackMixin {
	
	private boolean hidden; // Forge: Allow packs to be hidden from the UI entirely
	
	@Inject(method = "<init>", at = @At("TAIL"))
	public void thiz(String string, boolean bl, Pack.ResourcesSupplier resourcesSupplier, Component component, Pack.Info info, PackCompatibility packCompatibility, Pack.Position position, boolean bl2, PackSource packSource, CallbackInfo ci) {
		this.hidden = info.hidden();
	}
	
	@Redirect(method = "readPackInfo", at = @At(value = "NEW", target = "(Lnet/minecraft/network/chat/Component;ILnet/minecraft/world/flag/FeatureFlagSet;)Lnet/minecraft/server/packs/repository/Pack$Info;"))
	private static Pack.Info readPackInfo$info$init(Component component, int i, FeatureFlagSet featureFlagSet,
													@Local PackResources packResources,
	                                                @Local PackMetadataSection packMetadataSection) {
		return new Pack.Info(component, packMetadataSection.getPackFormat(PackType.SERVER_DATA),
			packMetadataSection.getPackFormat(PackType.CLIENT_RESOURCES), featureFlagSet,
			packResources.isHidden());
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	@Mixin(Pack.Info.class)
	public static abstract class InfoMixin {
		
		@Shadow @Final private Component description;
		@Shadow @Final private FeatureFlagSet requestedFeatures;
		@ShadowConstructor
		public abstract void thiz(Component description, int format, FeatureFlagSet requestedFeatures);
		
		public int dataFormat;
		public int dataFormat() {
			return this.dataFormat;
		}
		
		public int resourceFormat;
		public int resourceFormat() {
			return this.resourceFormat;
		}
		
		public boolean hidden;
		public boolean hidden() {
			return this.hidden;
		}
		
		@Inject(method = "<init>(Lnet/minecraft/network/chat/Component;ILnet/minecraft/world/flag/FeatureFlagSet;)V",
			at = @At("TAIL"))
		public void thiz(Component component, int i, FeatureFlagSet featureFlagSet, CallbackInfo ci) {
			this.dataFormat = i;
			this.resourceFormat = i;
			this.hidden = false;
		}
		
		@NewConstructor
		@ReplaceConstructor
		public void thiz(Component description, int dataFormat, int resourceFormat, FeatureFlagSet requestedFeatures,
		                 boolean hidden) {
			thiz(description, dataFormat, requestedFeatures);
			this.dataFormat = dataFormat;
			this.resourceFormat = resourceFormat;
			this.hidden = hidden;
		}
		
		public int getFormat(PackType packType) {
			return packType == PackType.SERVER_DATA ? this.dataFormat : this.resourceFormat;
		}
		
		@ModifyArg(method = "compatibility", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs" +
			"/repository/PackCompatibility;forFormat(ILnet/minecraft/server/packs/PackType;)" +
			"Lnet/minecraft/server/packs/repository/PackCompatibility;"), index = 0)
		public int compatibility$forFormat$0(int i, @Local PackType packType) {
			return getFormat(packType);
		}
		
	}
	
}
