package net.feltmc.neoforge.patches.mixin.client.gui.screens.worldselection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldSelectionList.class)
public class WorldSelectionListMixin extends ObjectSelectionList<WorldSelectionList.Entry> {
	public WorldSelectionListMixin(Minecraft minecraft, int i, int j, int k, int l, int m) {
		super(minecraft, i, j, k, l, m);
	}
	
	private static final ResourceLocation FORGE_EXPERIMENTAL_WARNING_ICON = new ResourceLocation("forge","textures/gui/experimental_warning.png");
}
