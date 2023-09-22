package net.feltmc.neoforge.patches.mixin;

import net.minecraft.server.packs.AbstractPackResources;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractPackResources.class)
public class AbstractPackResourcesMixin {
    @Shadow @Final private String name;

    @Override
    public String toString() {
        return String.format(java.util.Locale.ROOT, "%s: %s", getClass().getName(), this.name);
    }
}
