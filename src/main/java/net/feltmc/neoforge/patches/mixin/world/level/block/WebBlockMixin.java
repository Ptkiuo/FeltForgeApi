package net.feltmc.neoforge.patches.mixin.world.level.block;

import net.minecraft.world.level.block.WebBlock;
import net.minecraftforge.common.IForgeShearable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WebBlock.class)
public class WebBlockMixin implements IForgeShearable {
}
