package net.feltmc.neoforge.patches.mixin.world.entity.vehicle;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.extensions.IForgeAbstractMinecart;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("MissingUnique")
@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin extends Entity implements IForgeAbstractMinecart {
    public AbstractMinecartMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow protected abstract double getMaxSpeed();

    private boolean canUseRail = true;
    private float currentSpeedCapOnRail = getMaxCartSpeedOnRail();
    @Nullable private Float maxSpeedAirLateral = null;
    private float maxSpeedAirVertical = DEFAULT_MAX_SPEED_AIR_VERTICAL;
    private double dragAir = DEFAULT_AIR_DRAG;

    @Override
    public double getMaxSpeedWithRail() { //Non-default because getMaximumSpeed is protected
        if (!canUseRail()) return getMaxSpeed();
        BlockPos pos = this.getCurrentRailPosition();
        BlockState state = this.level().getBlockState(pos);
        if (!state.is(BlockTags.RAILS)) return getMaxSpeed();

        //float railMaxSpeed = ((BaseRailBlock)state.getBlock()).getRailMaxSpeed(state, this.level(), pos, this);
        //return Math.min(railMaxSpeed, getCurrentCartSpeedCapOnRail());
        return getCurrentCartSpeedCapOnRail();
    }
    @Override
    public void moveMinecartOnRail(BlockPos pos) { //Non-default because getMaximumSpeed is protected
        double d24 = this.isVehicle() ? 0.75D : 1.0D;
        double d25 = this.getMaxSpeedWithRail();
        Vec3 vec3d1 = this.getDeltaMovement();
        this.move(MoverType.SELF, new Vec3(Mth.clamp(d24 * vec3d1.x, -d25, d25), 0.0D, Mth.clamp(d24 * vec3d1.z, -d25, d25)));
    }

    @Override
    public boolean canUseRail() {
        return canUseRail;
    }

    @Override
    public void setCanUseRail(boolean use) {
        this.canUseRail = use;
    }

    @Override
    public float getCurrentCartSpeedCapOnRail() {
        return currentSpeedCapOnRail;
    }

    @Override
    public void setCurrentCartSpeedCapOnRail(float value) {
        currentSpeedCapOnRail = value;
    }

    @Override
    public float getMaxSpeedAirLateral() {
        return maxSpeedAirLateral == null ? (float) getMaxSpeed() : maxSpeedAirLateral;
    }

    @Override
    public void setMaxSpeedAirLateral(float value) {
        maxSpeedAirLateral = value;
    }

    @Override
    public float getMaxSpeedAirVertical() {
        return maxSpeedAirVertical;
    }

    @Override
    public void setMaxSpeedAirVertical(float value) {
        maxSpeedAirVertical = value;
    }

    @Override
    public double getDragAir() {
        return dragAir;
    }

    @Override
    public void setDragAir(double value) {
        dragAir = value;
    }
}
