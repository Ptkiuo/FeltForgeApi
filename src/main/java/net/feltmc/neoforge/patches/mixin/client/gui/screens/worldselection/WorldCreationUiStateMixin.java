package net.feltmc.neoforge.patches.mixin.client.gui.screens.worldselection;

import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.Optional;

@Mixin(WorldCreationUiState.class)
public class WorldCreationUiStateMixin {
	@Redirect(method = "getPresetEditor", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private Object replacePresetEditor(Map instance, Object o) {
		Optional<ResourceKey<WorldPreset>> key = (Optional<ResourceKey<WorldPreset>>) o;
		return key.map(net.minecraftforge.client.PresetEditorManager::get)
			.orElse(null);
	}
}
