package net.feltmc.neoforge.patches.interfaces;

import net.feltmc.neoforge.FeltVars;
import net.minecraft.client.Minecraft;

public interface ScreenInterface {
    default Minecraft getMinecraft() {
	    throw new RuntimeException(FeltVars.mixinOverrideException);
    }
}
