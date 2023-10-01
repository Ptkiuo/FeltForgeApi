package net.feltmc.neoforge.patches.interfaces;

import net.feltmc.neoforge.FeltVars;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;

public interface CreativeModeInventoryScreen_SlotWrapperInterface {
	default int getSlotIndex() {
		throw new RuntimeException(FeltVars.mixinOverrideException);
	}
	
	default boolean isSameInventory(Slot other) {
		throw new RuntimeException(FeltVars.mixinOverrideException);
	}
	
	default Slot setBackground(ResourceLocation atlas, ResourceLocation sprite) {
		throw new RuntimeException(FeltVars.mixinOverrideException);
	}
}
