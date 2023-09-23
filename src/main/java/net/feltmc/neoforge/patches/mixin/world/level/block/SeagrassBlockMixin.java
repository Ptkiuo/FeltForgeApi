package net.feltmc.neoforge.patches.mixin.world.level.block;

import net.minecraft.world.level.block.SeagrassBlock;
import net.minecraftforge.common.IForgeShearable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SeagrassBlock.class)
public class SeagrassBlockMixin implements IForgeShearable {
}
