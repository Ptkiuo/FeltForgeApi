package net.feltmc.neoforge.patches.interfaces;

import net.feltmc.neoforge.FeltVars;

public interface MapDecorationInterface {
    default boolean render(int index) {
        throw new RuntimeException(FeltVars.mixinOverrideException);
    }
}
