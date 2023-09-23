package net.feltmc.neoforge.patches.mixin.world.item;

import net.feltmc.neoforge.patches.interfaces.ArrowItemInterface;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ArrowItem.class)
public class ArrowItemMixin implements ArrowItemInterface {
    @Override
    public boolean isInfinite(ItemStack stack, ItemStack bow, Player player) {
        int enchant = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bow);
        return enchant <= 0 ? false : ((ArrowItem) (Object) this).getClass() == ArrowItem.class;
    }
}
