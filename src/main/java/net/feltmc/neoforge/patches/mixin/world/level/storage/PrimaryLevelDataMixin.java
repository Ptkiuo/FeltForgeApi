package net.feltmc.neoforge.patches.mixin.world.level.storage;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelVersion;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("MissingUnique")
@Mixin(PrimaryLevelData.class)
public class PrimaryLevelDataMixin {
	
	@Shadow
	private LevelSettings settings;
	private boolean confirmedExperimentalWarning = false;
	
	@Inject(method = "parse", at = @At("RETURN"))
	private static void parse$TAIL(Dynamic<?> dynamic, DataFixer dataFixer, int i, 
                                   @Nullable CompoundTag compoundTag, LevelSettings levelSettings, 
                                   LevelVersion levelVersion, @SuppressWarnings("deprecation") 
                                       PrimaryLevelData.SpecialWorldProperty specialWorldProperty, 
                                   WorldOptions worldOptions, Lifecycle lifecycle, 
                                   CallbackInfoReturnable<PrimaryLevelData> cir) {
		cir.getReturnValue()
			.withConfirmedWarning(
				lifecycle != Lifecycle.stable() && 
				dynamic.get("confirmedExperimentalSettings").asBoolean(false));
	}
	
	@Inject(method = "setTagData", at = @At("TAIL"))
	public void setTagData$TAIL(RegistryAccess registryAccess, CompoundTag compoundTag, 
	                            CompoundTag compoundTag2, CallbackInfo ci) {
		compoundTag.putString("forgeLifecycle", ForgeHooks.encodeLifecycle(this.settings.getLifecycle()));
		compoundTag.putBoolean("confirmedExperimentalSettings", this.confirmedExperimentalWarning);
	}
	
	public boolean hasConfirmedExperimentalWarning() {
		return this.confirmedExperimentalWarning;
	}
	
	public PrimaryLevelData withConfirmedWarning(boolean confirmedWarning) {
		this.confirmedExperimentalWarning = confirmedWarning;
		return (PrimaryLevelData) (Object) this;
	}
	
}
