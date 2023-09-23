package net.feltmc.neoforge.patches.mixin.world.level.block;

import net.minecraft.world.level.block.BushBlock;
import net.minecraftforge.common.IForgeShearable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BushBlock.class)
public class BushBlockMixin implements IForgeShearable {
}
