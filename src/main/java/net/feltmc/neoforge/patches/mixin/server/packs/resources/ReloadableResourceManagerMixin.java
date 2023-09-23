package net.feltmc.neoforge.patches.mixin.server.packs.resources;

import net.feltmc.neoforge.patches.interfaces.ReloadableResourceManagerInterface;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ReloadableResourceManager.class)
public abstract class ReloadableResourceManagerMixin implements ReloadableResourceManagerInterface {
    @Shadow @Final private List<PreparableReloadListener> listeners;

    @Shadow public abstract void registerReloadListener(PreparableReloadListener preparableReloadListener);

    public void registerReloadListenerIfNotPresent(PreparableReloadListener listener) {
       if (!this.listeners.contains(listener)) {
           this.registerReloadListener(listener);
       }
    }
}
