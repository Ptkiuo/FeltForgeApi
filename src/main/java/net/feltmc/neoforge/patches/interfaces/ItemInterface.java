package net.feltmc.neoforge.patches.interfaces;

import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public interface ItemInterface {
	
	default void initializeClient(Consumer<IClientItemExtensions> consumer) {
		throw new AssertionError();
	}
	
}
