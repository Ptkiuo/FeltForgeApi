package net.feltmc.neoforge.patches.interfaces;

import net.feltmc.neoforge.FeltVars;

public interface AbstractWidgetInterface {
    default void setHeight(int value) {
	    throw new RuntimeException(FeltVars.mixinOverrideException);
    }
    default int getFGColor() {
	    throw new RuntimeException(FeltVars.mixinOverrideException);
    }
    default void setFGColor(int color) {
	    throw new RuntimeException(FeltVars.mixinOverrideException);
    }
    default void clearFGColor() {
	    throw new RuntimeException(FeltVars.mixinOverrideException);
    }
}
