package net.feltmc.neoforge.patches.mixin.world.level.dimension.end;

import net.feltmc.neoforge.patches.interfaces.EndDragonFightInterface;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EndDragonFight.class)
public class EndDragonFightMixin implements EndDragonFightInterface {
    @Shadow @Final private ServerBossEvent dragonEvent;

    public void addPlayer(ServerPlayer player) {
        this.dragonEvent.addPlayer(player);
    }

    public void removePlayer(ServerPlayer player) {
        this.dragonEvent.removePlayer(player);
    }
}
