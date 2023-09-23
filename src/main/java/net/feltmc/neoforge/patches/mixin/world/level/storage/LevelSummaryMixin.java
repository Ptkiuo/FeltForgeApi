package net.feltmc.neoforge.patches.mixin.world.level.storage;

import net.feltmc.neoforge.patches.interfaces.LevelSummaryInterface;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LevelSummary.class)
public class LevelSummaryMixin implements LevelSummaryInterface {
    @Shadow @Final private LevelSettings settings;

    @Override
    public boolean isLifecycleExperimental() {
        return this.settings.getLifecycle().equals(com.mojang.serialization.Lifecycle.experimental());
    }
}
