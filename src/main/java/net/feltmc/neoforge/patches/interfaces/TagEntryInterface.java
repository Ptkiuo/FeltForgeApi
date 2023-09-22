package net.feltmc.neoforge.patches.interfaces;

import net.feltmc.neoforge.FeltVars;
import net.minecraft.resources.ResourceLocation;

public interface TagEntryInterface {
    default ResourceLocation getId() {
        throw new RuntimeException(FeltVars.mixinOverrideException);
    }

    default boolean isRequired() {
        throw new RuntimeException(FeltVars.mixinOverrideException);
    }

    default boolean isTag() {
        throw new RuntimeException(FeltVars.mixinOverrideException);
    }
}
