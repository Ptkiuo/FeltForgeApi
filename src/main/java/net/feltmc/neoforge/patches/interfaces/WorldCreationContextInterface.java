package net.feltmc.neoforge.patches.interfaces;

import net.feltmc.neoforge.FeltVars;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.world.level.WorldDataConfiguration;

public interface WorldCreationContextInterface {
	default WorldCreationContext withDataConfiguration(WorldDataConfiguration dataConfiguration) {
		throw new RuntimeException(FeltVars.mixinOverrideException);
	}
}
