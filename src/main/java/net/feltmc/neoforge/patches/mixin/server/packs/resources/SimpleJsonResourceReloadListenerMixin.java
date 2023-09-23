package net.feltmc.neoforge.patches.mixin.server.packs.resources;

import net.feltmc.neoforge.patches.interfaces.SimpleJsonResourceReloadListenerInterface;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SimpleJsonResourceReloadListener.class)
public class SimpleJsonResourceReloadListenerMixin implements SimpleJsonResourceReloadListenerInterface{
    @Shadow @Final private String directory;

    @Override
    public ResourceLocation getPreparedPath(ResourceLocation rl) {
        return rl.withPath(this.directory + "/" + rl.getPath() + ".json");
    }
}
