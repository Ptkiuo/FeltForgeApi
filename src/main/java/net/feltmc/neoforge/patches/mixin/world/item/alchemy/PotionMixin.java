package net.feltmc.neoforge.patches.mixin.world.item.alchemy;

import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.common.extensions.IForgePotion;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Potion.class)
public class PotionMixin implements IForgePotion {
}
