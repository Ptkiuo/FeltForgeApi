package net.feltmc.neoforge.patches.mixin.client.gui.screens.worldselection;

import com.mojang.serialization.Lifecycle;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.DatapackLoadFailureScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(WorldOpenFlows.class)
public abstract class WorldOpenFlowsMixin {
	
	@Shadow
	@Nullable
	protected abstract LevelStorageSource.LevelStorageAccess createWorldAccess(String string);
	
	@Shadow
	protected abstract WorldStem loadWorldStem(LevelStorageSource.LevelStorageAccess levelStorageAccess, boolean bl, PackRepository packRepository) throws Exception;
	
	@Shadow
	@Final
	private static Logger LOGGER;
	
	@Shadow
	@Final
	private Minecraft minecraft;
	
	@Shadow
	protected abstract void doLoadLevel(Screen screen, String string, boolean bl, boolean bl2);
	
	@Shadow
	protected abstract void safeCloseAccess(LevelStorageSource.LevelStorageAccess levelStorageAccess, String string);
	
	@Shadow
	protected abstract CompletableFuture<Boolean> promptBundledPackLoadFailure();
	
	@Shadow
	protected abstract void askForBackup(Screen screen, String string, boolean bl, Runnable runnable);
	
	@Inject(method = "doLoadLevel", at = @At("HEAD"), cancellable = true)
	private void doLoadLevel$override(Screen screen, String string, boolean bl, boolean bl2, CallbackInfo ci) {
		// FORGE: Patch in overload to reduce further patching
		this.doLoadLevel(screen, string, bl, bl2, false);
		ci.cancel();
	}
	
	// FORGE: Patch in confirmExperimentalWarning which confirms the experimental warning when true
	private void doLoadLevel(Screen screen, String string, boolean bl, boolean bl2, boolean confirmExperimentalWarning) {
		LevelStorageSource.LevelStorageAccess levelStorageAccess = this.createWorldAccess(string);
		if (levelStorageAccess != null) {
			PackRepository packRepository = ServerPacksSource.createPackRepository(levelStorageAccess);
			
			WorldStem worldStem;
			try {
				levelStorageAccess.readAdditionalLevelSaveData(); // Read extra (e.g. modded) data from the world before creating it
				worldStem = this.loadWorldStem(levelStorageAccess, bl, packRepository);
				if (confirmExperimentalWarning && worldStem.worldData() instanceof PrimaryLevelData pld) {
					pld.withConfirmedWarning(true);
				}
			} catch (Exception var11) {
				LOGGER.warn((String)"Failed to load level data or datapacks, can't proceed with server load",
					(Throwable)var11);
				if (!bl) {
					this.minecraft.setScreen(new DatapackLoadFailureScreen(() -> {
						this.doLoadLevel(screen, string, true, bl2);
					}));
				} else {
					this.minecraft.setScreen(new AlertScreen(() -> {
						this.minecraft.setScreen((Screen)null);
					}, Component.translatable("datapackFailure.safeMode.failed.title"), Component.translatable("datapackFailure.safeMode.failed.description"), CommonComponents.GUI_TO_TITLE, true));
				}
				
				safeCloseAccess(levelStorageAccess, string);
				return;
			}
			
			WorldData worldData = worldStem.worldData();
			boolean bl3 = worldData.worldGenOptions().isOldCustomizedWorld();
			boolean bl4 = worldData.worldGenSettingsLifecycle() != Lifecycle.stable();
			// Forge: Skip confirmation if it has been done already for this world
			boolean skipConfirmation = worldData instanceof PrimaryLevelData pld 
				&& pld.hasConfirmedExperimentalWarning();
			if (skipConfirmation || !bl2 || !bl3 && !bl4) {
				this.minecraft.getDownloadedPackSource().loadBundledResourcePack(levelStorageAccess).thenApply((void_) -> {
					return true;
				}).exceptionallyComposeAsync((throwable) -> {
					LOGGER.warn("Failed to load pack: ", throwable);
					return this.promptBundledPackLoadFailure();
				}, this.minecraft).thenAcceptAsync((boolean_) -> {
					if (boolean_) {
						this.minecraft.doWorldLoad(string, levelStorageAccess, packRepository, worldStem, false);
					} else {
						worldStem.close();
						safeCloseAccess(levelStorageAccess, string);
						this.minecraft.getDownloadedPackSource().clearServerPack().thenRunAsync(() -> {
							this.minecraft.setScreen(screen);
						}, this.minecraft);
					}
					
				}, this.minecraft).exceptionally((throwable) -> {
					this.minecraft.delayCrash(CrashReport.forThrowable(throwable, "Load world"));
					return null;
				});
			} else {
				if (bl3) { // Forge: For legacy world options, let vanilla handle it.
					this.askForBackup(screen, string, bl3, () -> {
						this.doLoadLevel(screen, string, bl, false);
					});
				} else {
					net.minecraftforge.client.ForgeHooksClient.createWorldConfirmationScreen(() -> this.doLoadLevel(screen, 
						string, bl, false, true));
				}
				
				worldStem.close();
				safeCloseAccess(levelStorageAccess, string);
			}
		}
	}
}
