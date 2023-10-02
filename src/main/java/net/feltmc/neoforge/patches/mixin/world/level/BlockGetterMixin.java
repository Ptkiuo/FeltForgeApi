package net.feltmc.neoforge.patches.mixin.world.level;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockGetter.class)
public class BlockGetterMixin implements IForgeBlockGetter {
	@WrapOperation(method = "getLightEmission", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state" +
		"/BlockState;getLightEmission()I"))
	public int getLightEmission$getLightEmission(BlockState instance, Operation<Integer> original,
	                                             @Local BlockPos blockPos) {
		return instance.getLightEmission((BlockGetter) (Object) this, blockPos);
	}
}