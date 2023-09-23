package net.feltmc.neoforge.patches.mixin.world.level.block.state;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockState.class)
public class BlockStateMixin implements IForgeBlockState {
}
