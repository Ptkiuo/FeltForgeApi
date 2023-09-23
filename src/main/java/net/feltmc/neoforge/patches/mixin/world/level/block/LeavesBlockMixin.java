package net.feltmc.neoforge.patches.mixin.world.level.block;

import net.minecraft.world.level.block.LeavesBlock;
import net.minecraftforge.common.IForgeShearable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin implements IForgeShearable {
}
