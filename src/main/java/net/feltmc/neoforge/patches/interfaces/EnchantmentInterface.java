package net.feltmc.neoforge.patches.interfaces;

import net.feltmc.neoforge.FeltVars;
import net.minecraft.world.item.ItemStack;

public interface EnchantmentInterface {
    default boolean canApplyAtEnchantingTable(ItemStack stack) {
	    throw new RuntimeException(FeltVars.mixinOverrideException);
    }
    default boolean isAllowedOnBooks() {
	    throw new RuntimeException(FeltVars.mixinOverrideException);
    }
}
