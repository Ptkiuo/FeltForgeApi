package net.feltmc.neoforge.patches.interfaces;

import net.feltmc.neoforge.FeltVars;

public interface CreativeModeInventoryScreenInterface {
	default net.minecraftforge.client.gui.CreativeTabsScreenPage getCurrentPage() {
		throw new RuntimeException(FeltVars.mixinOverrideException);
	}
	
	default void setCurrentPage(net.minecraftforge.client.gui.CreativeTabsScreenPage currentPage) {
		throw new RuntimeException(FeltVars.mixinOverrideException);
	}
}
