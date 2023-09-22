package net.feltmc.neoforge.patches.mixin;

import net.feltmc.neoforge.FeltVars;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends Projectile {
    public FireworkRocketEntityMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
        throw new RuntimeException(FeltVars.fakeConstructorException);
    }


    @Override
    public void onHit(HitResult result) {
        if (result.getType() == HitResult.Type.MISS || !ForgeEventFactory.onProjectileImpact(((FireworkRocketEntity) (Object) this), result)) {
            super.onHit(result);
        }
    }
}
