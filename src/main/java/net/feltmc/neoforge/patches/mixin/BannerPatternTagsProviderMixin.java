package net.feltmc.neoforge.patches.mixin;

import fr.catcore.cursedmixinextensions.annotations.NewConstructor;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BannerPatternTagsProvider;
import org.spongepowered.asm.mixin.Mixin;

import java.util.concurrent.CompletableFuture;

@Mixin(BannerPatternTagsProvider.class)
public class BannerPatternTagsProviderMixin {

    @NewConstructor
    public void BannerPatternTagsProvider(PackOutput p_256451_, CompletableFuture<HolderLookup.Provider> p_256420_, String modId, @org.jetbrains.annotations.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
        super(p_256451_, Registries.BANNER_PATTERN, p_256420_, modId, existingFileHelper); //TODO felt what?
    }
}
