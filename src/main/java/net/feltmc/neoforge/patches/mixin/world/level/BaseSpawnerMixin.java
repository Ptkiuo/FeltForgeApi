package net.feltmc.neoforge.patches.mixin.world.level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("MissingUnique")
@Mixin(BaseSpawner.class)
public class BaseSpawnerMixin {
	
	@Nullable
	public Entity getSpawnerEntity() {
		return null;
	}
	
	@Nullable
	public BlockEntity getSpawnerBlockEntity() {
		return null;
	}
	
}
