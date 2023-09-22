package net.feltmc.neoforge.patches.interfaces;

import net.feltmc.neoforge.FeltVars;
import net.minecraft.core.particles.BlockParticleOption;

public interface BlockParticleOptionInterface {
        default BlockParticleOption setPos(net.minecraft.core.BlockPos pos) {
            throw new RuntimeException(FeltVars.mixinOverrideException);
        }

        default net.minecraft.core.BlockPos getPos() {
            throw new RuntimeException(FeltVars.mixinOverrideException);
        }
}
