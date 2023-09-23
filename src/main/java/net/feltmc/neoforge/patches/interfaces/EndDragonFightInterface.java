package net.feltmc.neoforge.patches.interfaces;

import net.feltmc.neoforge.FeltVars;
import net.minecraft.server.level.ServerPlayer;

public interface EndDragonFightInterface {
    default void addPlayer(ServerPlayer player) {
        throw new RuntimeException(FeltVars.mixinOverrideException);
    }

    default void removePlayer(ServerPlayer player) {
        throw new RuntimeException(FeltVars.mixinOverrideException);
    }
}
