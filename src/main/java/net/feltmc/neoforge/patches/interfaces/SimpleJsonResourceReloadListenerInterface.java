package net.feltmc.neoforge.patches.interfaces;

import net.feltmc.neoforge.FeltVars;
import net.minecraft.resources.ResourceLocation;

public interface SimpleJsonResourceReloadListenerInterface {
    default ResourceLocation getPreparedPath(ResourceLocation rl) {
        throw new RuntimeException(FeltVars.mixinOverrideException);
    }
}
