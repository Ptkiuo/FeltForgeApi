package net.feltmc.neoforge.patches.interfaces;

import net.feltmc.neoforge.FeltVars;
import net.minecraft.client.gui.GuiGraphics;

public interface GuiInterface {
    default void renderSelectedItemName(GuiGraphics guiGraphics, int yShift) {
	    throw new RuntimeException(FeltVars.mixinOverrideException);
    }
}
