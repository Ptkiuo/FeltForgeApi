package net.feltmc.neoforge.patches.mixin.world.level.block;

import net.minecraft.world.level.block.BucketPickup;
import net.minecraftforge.common.extensions.IForgeBucketPickup;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BucketPickup.class)
public class BucketPickupMixin implements IForgeBucketPickup {
}
