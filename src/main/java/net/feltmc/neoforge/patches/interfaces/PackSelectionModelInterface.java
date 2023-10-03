package net.feltmc.neoforge.patches.interfaces;

public interface PackSelectionModelInterface {
	public static interface EntryInterface {
		default boolean notHidden() { return true; }
	}
}
