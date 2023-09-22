package net.feltmc.neoforge.patches.interfaces;

import net.feltmc.neoforge.FeltVars;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public interface ReloadableResourceManagerInterface {
    default void registerReloadListenerIfNotPresent(PreparableReloadListener listener) {
        throw new RuntimeException(FeltVars.mixinOverrideException);
    }
}
