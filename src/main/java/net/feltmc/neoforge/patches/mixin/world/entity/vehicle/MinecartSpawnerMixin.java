package net.feltmc.neoforge.patches.mixin.world.entity.vehicle;

import net.feltmc.neoforge.FeltVars;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MinecartSpawner.class)
public abstract class MinecartSpawnerMixin extends AbstractMinecart {
    protected MinecartSpawnerMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
        throw new RuntimeException(FeltVars.fakeConstructorException);
    }

    //TODO felt why is this an override?
    @Override
    @Nullable
    public net.minecraft.world.entity.Entity getSpawnerEntity() {
        return ((MinecartSpawner) (Object) this);
    }
}
