package net.feltmc.neoforge.patches.mixin.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin extends Screen {
	@Shadow
	private @Nullable PackRepository tempDataPackRepository;
	
	protected CreateWorldScreenMixin(Component component) {
		super(component);
	}
	
	@Inject(method = "openFresh", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;createDefaultLoadConfig(Lnet/minecraft/server/packs/repository/PackRepository;Lnet/minecraft/world/level/WorldDataConfiguration;)Lnet/minecraft/server/WorldLoader$InitConfig;"))
	private static void openFresh$event(Minecraft minecraft, Screen screen, CallbackInfo ci, 
	                                    @Local(ordinal = 0) PackRepository packrepository) {
		net.minecraftforge.fml.ModLoader.get()
			.postEvent(new net.minecraftforge.event.AddPackFindersEvent(
				net.minecraft.server.packs.PackType.SERVER_DATA, 
				packrepository::addPackFinder
			));
	}
	
	@Inject(method = "createNewWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;createWorldOpenFlows()Lnet/minecraft/client/gui/screens/worldselection/WorldOpenFlows;"))
	private void createNewWorld$(PrimaryLevelData.SpecialWorldProperty specialWorldProperty,
	                             LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess, Lifecycle lifecycle, CallbackInfo ci,
	                             @Local(ordinal = 0) WorldData worldData) {
		if(worldData.worldGenSettingsLifecycle() != Lifecycle.stable()) {
			// Neo: set experimental settings confirmation flag so user is not shown warning on next open
			((PrimaryLevelData)worldData).withConfirmedWarning(true);
		}
	}
	
	@ModifyArg(method = "method_48654", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept" +
		"(Ljava/lang/Object;)V", ordinal = 1))
	private <T> T revertToActualVanillaData(T t) {
		// FORGE: Revert to *actual* vanilla data
		return (T) new WorldDataConfiguration(new DataPackConfig(ImmutableList.of("vanilla"), ImmutableList.of()),
			FeatureFlags.DEFAULT_FLAGS);
	}
	
	@Inject(method = "getDataPackSelectionSettings", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;reload()V"))
	private void getDataPackSelectionSettings$loadForge(WorldDataConfiguration worldDataConfiguration, 
	                                                    CallbackInfoReturnable<@Nullable Pair<Path, PackRepository>> cir) {
		net.minecraftforge.resource.ResourcePackLoader.loadResourcePacks(
			this.tempDataPackRepository, 
			net.minecraftforge.server.ServerLifecycleHooks::buildPackFinder
		);
	}
}
