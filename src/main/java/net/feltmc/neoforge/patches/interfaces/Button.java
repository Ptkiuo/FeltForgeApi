package net.feltmc.neoforge.patches.interfaces;

import net.feltmc.neoforge.FeltVars;

public interface Button {
	
	public interface BuilderInterface {
		default net.minecraft.client.gui.components.Button build(java.util.function.Function<net.minecraft.client.gui.components.Button.Builder, net.minecraft.client.gui.components.Button> builder) {
			throw new RuntimeException(FeltVars.mixinOverrideException);
		}
	}
	
}
