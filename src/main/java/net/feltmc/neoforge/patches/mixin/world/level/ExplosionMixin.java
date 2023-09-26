package net.feltmc.neoforge.patches.mixin.world.level;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@SuppressWarnings("MissingUnique")
@Mixin(Explosion.class)
public class ExplosionMixin {
	
	@Shadow @Final private double x;
	@Shadow @Final private double y;
	@Shadow @Final private double z;
	@Shadow @Final private Level level;
	@Shadow @Final private @Nullable Entity source;
	
	@SuppressWarnings("FieldCanBeLocal")
	private Vec3 position;
	
	@Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;DDDFLjava/util/List;)V", at = @At("TAIL"))
	public void thiz(Level level, Entity entity, double d, double e, double f, float g, List<BlockPos> list, CallbackInfo ci) {
		position = new Vec3(x, y, z);
	}
	
	@Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;", shift = At.Shift.AFTER))
	public void explode$getEntities(CallbackInfo ci, @Local List<Entity> list, @Local(ordinal = 1) float q) {
		ForgeEventFactory.onExplosionDetonate(level, (Explosion) (Object) this, list, q);
	}
	
	@WrapOperation(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;dropFromExplosion(Lnet/minecraft/world/level/Explosion;)Z"))
	public boolean finalizeExplosion$dropFromExplosion(Block instance, Explosion explosion, Operation<Boolean> original, @Local BlockState blockState, @Local BlockPos blockPos) {
		return blockState.canDropFromExplosion(level, blockPos, explosion);
	}
	
	@Redirect(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
	public boolean finalizeExplosion$setBlock(Level instance, BlockPos blockPos, BlockState blockState, int i) {
		return false;
	}
	
	@Redirect(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;wasExploded(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;)V"))
	public void finalizeExplosion$wasExploded(Block instance, Level level, BlockPos blockPos, Explosion explosion, @Local BlockState blockState) {
		blockState.onBlockExploded(level, blockPos, explosion);
	}
	
	public Vec3 getPosition() {
		return position;
	}
	
	@Nullable
	public Entity getExploder() {
		return source;
	}
	
}
