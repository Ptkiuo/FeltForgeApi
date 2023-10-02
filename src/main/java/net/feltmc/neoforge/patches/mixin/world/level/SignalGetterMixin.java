package net.feltmc.neoforge.patches.mixin.world.level;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SignalGetter.class)
public class SignalGetterMixin {
	@WrapOperation(method = "getSignal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state" +
		"/BlockState;isRedstoneConductor(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z"))
	boolean getSignal$isRedstoneConductor(BlockState instance, BlockGetter blockGetter, BlockPos blockPos,
	                                      Operation<Boolean> original, @Local Direction direction) {
		return instance.shouldCheckWeakPower((SignalGetter) (Object) this, blockPos, direction);
	}
}
