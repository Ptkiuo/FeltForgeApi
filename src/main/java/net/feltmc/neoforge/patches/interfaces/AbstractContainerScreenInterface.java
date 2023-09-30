package net.feltmc.neoforge.patches.interfaces;

import net.feltmc.neoforge.FeltVars;
import net.minecraft.world.inventory.Slot;

public interface AbstractContainerScreenInterface {
	@org.jetbrains.annotations.Nullable default Slot getSlotUnderMouse() {
		throw new RuntimeException(FeltVars.mixinOverrideException);
	}
	
	default int getGuiLeft() {
		throw new RuntimeException(FeltVars.mixinOverrideException);
	}
	
	default int getGuiTop() {
		throw new RuntimeException(FeltVars.mixinOverrideException);
	}
	
	default int getXSize() {
		throw new RuntimeException(FeltVars.mixinOverrideException);
	}
	
	default int getYSize() {
		throw new RuntimeException(FeltVars.mixinOverrideException);
	}
	
	default int getSlotColor(int index) {
		throw new RuntimeException(FeltVars.mixinOverrideException);
	}
}
