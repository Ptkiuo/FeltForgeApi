package net.feltmc.neoforge.patches.interfaces;

import net.feltmc.neoforge.FeltVars;
import net.minecraft.client.gui.components.Button;

public interface Button$BuilderInterface {
    default Button build(java.util.function.Function<Button.Builder, Button> builder) {
	    throw new RuntimeException(FeltVars.mixinOverrideException);
    }
}
