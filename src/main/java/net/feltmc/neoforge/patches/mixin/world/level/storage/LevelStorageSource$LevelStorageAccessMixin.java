package net.feltmc.neoforge.patches.mixin.world.level.storage;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

@SuppressWarnings("MissingUnique")
@Mixin(LevelStorageSource.LevelStorageAccess.class)
public abstract class LevelStorageSource$LevelStorageAccessMixin {
	@Shadow protected abstract void checkLock();
	
	@Shadow
	@Final
	// represents LevelStorageSource.this
	private LevelStorageSource field_23766;
	
	@Shadow
	@Final
	private LevelStorageSource.LevelDirectory levelDirectory;
	public void readAdditionalLevelSaveData() {
		checkLock();
		field_23766.readLevelData(levelDirectory, (path, dataFixer) -> {
			try {
				CompoundTag compoundTag = NbtIo.readCompressed(path.toFile());
				net.minecraftforge.common.ForgeHooks.readAdditionalLevelSaveData(compoundTag, levelDirectory);
			}
			catch (Exception e) {
				LevelStorageSource.LOGGER.error("Exception reading {}", path, e);
			}
			return ""; // Return non-null to prevent level.dat-old inject
		});
	}
	@Inject(method = "saveDataTag(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/storage/WorldData;" +
		"Lnet/minecraft/nbt/CompoundTag;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;put" +
		"(Ljava/lang/String;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;", shift = At.Shift.AFTER))
	public void saveDataTag$put(RegistryAccess registryAccess, WorldData worldData, @Nullable CompoundTag compoundTag
		, CallbackInfo ci, @Local(ordinal = 2) CompoundTag compoundTag3) {
		ForgeHooks.writeAdditionalLevelSaveData(worldData, compoundTag3);
	}
	
	public Path getWorldDir() {
		return field_23766.getBaseDir();
	}
}
