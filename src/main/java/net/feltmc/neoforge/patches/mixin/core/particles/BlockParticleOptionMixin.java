package net.feltmc.neoforge.patches.mixin.core.particles;

import net.feltmc.neoforge.patches.interfaces.BlockParticleOptionInterface;
import net.minecraft.core.particles.BlockParticleOption;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockParticleOption.class)
public class BlockParticleOptionMixin implements BlockParticleOptionInterface {
    private net.minecraft.core.BlockPos pos;

    @Override
    public BlockParticleOption setPos(net.minecraft.core.BlockPos pos) {
        return ((BlockParticleOption) (Object) this);
    }

    @Override
    public net.minecraft.core.BlockPos getPos() {
        return pos;
    }
}
