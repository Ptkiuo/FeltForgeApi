package net.feltmc.neoforge.patches.interfaces;

import net.feltmc.neoforge.FeltVars;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ArrowItemInterface {
    default boolean isInfinite(ItemStack stack, ItemStack bow, Player player) {
        throw new RuntimeException(FeltVars.mixinOverrideException);
    }
}
