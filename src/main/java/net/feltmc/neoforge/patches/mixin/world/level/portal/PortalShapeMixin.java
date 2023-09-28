package net.feltmc.neoforge.patches.mixin.world.level.portal;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PortalShape.class)
public class PortalShapeMixin {
	
	@Redirect(method = "method_30487", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
	private static boolean frame$is(BlockState instance, 
	                                Block block, 
	                                @Local BlockGetter blockGetter,
	                                @Local BlockPos blockPos) {
		return instance.isPortalFrame(blockGetter, blockPos);
	}
	
}
