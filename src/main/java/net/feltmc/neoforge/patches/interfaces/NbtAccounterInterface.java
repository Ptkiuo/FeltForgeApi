package net.feltmc.neoforge.patches.interfaces;

import net.feltmc.neoforge.FeltVars;

public interface NbtAccounterInterface {
    default String readUTF(String data) {
        throw new RuntimeException(FeltVars.mixinOverrideException);
    }
}
