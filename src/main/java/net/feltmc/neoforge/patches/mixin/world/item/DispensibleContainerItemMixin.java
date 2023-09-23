package net.feltmc.neoforge.patches.mixin.world.item;

import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraftforge.common.extensions.IForgeDispensibleContainerItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DispensibleContainerItem.class)
public class DispensibleContainerItemMixin implements IForgeDispensibleContainerItem {
}
