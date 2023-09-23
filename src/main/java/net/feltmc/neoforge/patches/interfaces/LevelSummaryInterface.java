package net.feltmc.neoforge.patches.interfaces;

import net.feltmc.neoforge.FeltVars;

public interface LevelSummaryInterface {
    default boolean isLifecycleExperimental() {
        throw new RuntimeException(FeltVars.mixinOverrideException);
    }
}
